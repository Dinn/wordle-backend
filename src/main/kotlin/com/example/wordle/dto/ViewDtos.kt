package com.example.wordle.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/** 최상위 래퍼 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ViewResponse(
    val channel: ViewChannel? = null
)

/** channel 내부 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ViewChannel(
    val total: String? = null,
    val item: ViewItem? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ViewItem(
    @JsonProperty("target_code")
    val targetCode: Long? = null,
    val wordInfo: WordInfo? = null,
    val senseInfo: SenseInfo? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WordInfo(
    val word: String? = null,
    @JsonProperty("word_type")  val wordType: String? = null,
    @JsonProperty("word_unit")  val wordUnit: String? = null,
    @JsonProperty("pronunciation_info") val pronunciationInfo: String? = null,
    @JsonProperty("pronunciation_link") val pronunciationLink: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SenseInfo(
    val definition: String? = null,
    @JsonProperty("definition_original") val definitionOriginal: String? = null,
    val pos: String? = null,
    val type: String? = null
)