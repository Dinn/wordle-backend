package com.example.wordle.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/** 최상위 래퍼 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchResponse(
    val channel: SearchChannel? = null
)

/** channel 내부 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchChannel(
    // 우리말샘 JSON은 문자열로 올 때가 있어 안전하게 String로 받음
    val total: String? = null,
    val start: String? = null,
    val num: String? = null,
    val item: List<SearchItem> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchItem(
    val word: String? = null,
    val sense: List<SearchSense> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchSense(
    @JsonProperty("target_code")
    val targetCode: Long? = null,
    @JsonProperty("sense_no")
    val senseNo: String? = null,
    val definition: String? = null,
    val pos: String? = null,
    val type: String? = null,
    val cat: String? = null
)