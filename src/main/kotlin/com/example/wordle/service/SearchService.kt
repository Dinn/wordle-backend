package com.example.wordle.service

import com.example.wordle.config.OpenDictProperties
import com.example.wordle.dto.SearchResponse
import com.example.wordle.dto.ViewResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Service
class SearchService(
    private val webClient: WebClient,
    private val props: OpenDictProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** ---------- Public APIs ---------- */

    fun fetchMeanings(q: String, onlyGeneral: Boolean = true): Mono<MeaningResult> {
        // 1) 동기 단계(URI 구성) 예외: try-catch
        val uri = try {
            UriComponentsBuilder.fromHttpUrl(props.search.baseUrl)
                .queryParam("certkey_no", props.search.certkeyNo)
                .queryParam("key", props.search.key)
                .queryParam("target_type", props.search.targetType)
                .queryParam("req_type", props.search.reqType) // json
                .queryParam("part", props.search.part)
                .queryParam("q", q.trim())
                .queryParam("sort", props.search.sort)
                .queryParam("start", props.search.start)
                .queryParam("num", props.search.num)
                .queryParam("advanced", props.search.advanced)
                .queryParam("method", props.search.method)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri()
        } catch (e: Exception) {
            log.error("OpenDict search URI build failed: ${e.message}", e)
            return Mono.just(fallbackMeaningResult(q))
        }

        // 2) 비동기 단계(WebClient) 예외: onStatus + onErrorResume
        return webClient.get()
            .uri(uri)
            .retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) { resp ->
                resp.bodyToMono(String::class.java)
                    .defaultIfEmpty("")
                    .flatMap { body ->
                        val msg = "OpenDict search HTTP ${resp.statusCode()} body=$body"
                        log.error(msg)
                        Mono.error(WebClientResponseException.create(
                            resp.statusCode().value(),
                            "Search API error",
                            null,
                            body.toByteArray(),
                            StandardCharsets.UTF_8
                        ))
                    }
            }
            .bodyToMono(SearchResponse::class.java)
            .defaultIfEmpty(SearchResponse())
            .map { resp ->
                val ch = resp.channel
                val senses = ch?.item?.flatMap { it.sense } ?: emptyList()

                val filtered = if (onlyGeneral)
                    senses.filter { it.type == null || it.type == "일반어" }
                else senses

                val defs = filtered.mapNotNull { it.definition?.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()
                val first = defs.firstOrNull().orEmpty()
                val oneDefList = if (first.isBlank()) emptyList() else listOf(first)

                val total = ch?.total?.toIntOrNull() ?: 0
                val start = ch?.start?.toIntOrNull() ?: props.search.start
                val num   = ch?.num?.toIntOrNull() ?: props.search.num

                log.debug(
                    "OpenDict search q='{}' total={} defs(all)={} -> defs(first)={}",
                    q, total, defs.size, oneDefList.size
                )

                MeaningResult(
                    query = q,
                    total = total,
                    start = start,
                    num = num,
                    count = oneDefList.size,
                    definitions = oneDefList
                )
            }
            .onErrorResume { ex ->
                log.error("OpenDict search failed for q='{}': {}", q, ex.message, ex)
                Mono.just(fallbackMeaningResult(q))
            }
    }

    fun fetchWordInfoByWordAndSense(qWordPlusSense: String): Mono<ViewResult> {
        // 1) 동기 단계(URI 구성) 예외: try-catch
        val uri = try {
            UriComponentsBuilder.fromHttpUrl(props.view.baseUrl)
                .queryParam("certkey_no", props.search.certkeyNo)
                .queryParam("key", props.search.key)
                .queryParam("target_type", props.view.targetType) // view
                .queryParam("req_type", props.view.reqType)       // json
                .queryParam("method", props.view.method)          // word_info
                .queryParam("q", qWordPlusSense.trim())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri()
        } catch (e: Exception) {
            log.error("OpenDict view URI build failed: ${e.message}", e)
            return Mono.just(fallbackViewResult(qWordPlusSense))
        }

        // 2) 비동기 단계(WebClient) 예외: onStatus + onErrorResume
        return webClient.get()
            .uri(uri)
            .retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) { resp ->
                resp.bodyToMono(String::class.java)
                    .defaultIfEmpty("")
                    .flatMap { body ->
                        val msg = "OpenDict view HTTP ${resp.statusCode()} body=$body"
                        log.error(msg)
                        Mono.error(WebClientResponseException.create(
                            resp.statusCode().value(),
                            "View API error",
                            null,
                            body.toByteArray(),
                            StandardCharsets.UTF_8
                        ))
                    }
            }
            .bodyToMono(ViewResponse::class.java)
            .defaultIfEmpty(ViewResponse())
            .map { resp ->
                val item = resp.channel?.item
                val word = item?.wordInfo?.word
                val def  = item?.senseInfo?.definition?.trim().orEmpty()

                ViewResult(
                    query = qWordPlusSense,
                    targetCode = item?.targetCode,
                    word = word,
                    definition = def
                )
            }
            .onErrorResume { ex ->
                log.error("OpenDict view failed for q='{}': {}", qWordPlusSense, ex.message, ex)
                Mono.just(fallbackViewResult(qWordPlusSense))
            }
    }

    /** ---------- Fallback Builders ---------- */

    private fun fallbackMeaningResult(q: String) = MeaningResult(
        query = q,
        total = 0,
        start = props.search.start,
        num = props.search.num,
        count = 0,
        definitions = emptyList()
    )

    private fun fallbackViewResult(q: String) = ViewResult(
        query = q,
        targetCode = null,
        word = null,
        definition = ""
    )
}

/** 반환 모델 (동일) */
data class MeaningResult(
    val query: String,
    val total: Int,
    val start: Int,
    val num: Int,
    val count: Int,
    val definitions: List<String>
)

data class ViewResult(
    val query: String,
    val targetCode: Long?,
    val word: String?,
    val definition: String
)