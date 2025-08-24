package com.example.wordle.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class WebMvcConfig {

    @Bean
    fun webClient(mapper: ObjectMapper): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected {
                it.addHandlerLast(ReadTimeoutHandler(5, TimeUnit.SECONDS))
                it.addHandlerLast(WriteTimeoutHandler(5, TimeUnit.SECONDS))
            }

        // ⚠️ vararg 로 전달 (List 아님)
        val decoder = Jackson2JsonDecoder(
            mapper,
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("text/json")
        )
        val encoder = Jackson2JsonEncoder(
            mapper,
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("text/json")
        )

        val strategies = ExchangeStrategies.builder()
            .codecs {
                it.defaultCodecs().jackson2JsonDecoder(decoder)
                it.defaultCodecs().jackson2JsonEncoder(encoder)
                it.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)
            }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .build()
    }
}