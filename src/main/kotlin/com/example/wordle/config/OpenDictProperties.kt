package com.example.wordle.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

data class OpenDictSearchProps(
    val baseUrl: String,
    val certkeyNo: String,
    val key: String,
    val targetType: String,
    val reqType: String,
    val part: String,
    val sort: String,
    val start: Int,
    val num: Int,
    val advanced: String,
    val method: String
)

data class OpenDictViewProps(
    val baseUrl: String,
    val targetType: String,
    val reqType: String,
    val method: String
)

@ConfigurationProperties(prefix = "app.opendict")
data class OpenDictProperties(
    val search: OpenDictSearchProps,
    val view: OpenDictViewProps
)

@Component
@EnableConfigurationProperties(OpenDictProperties::class)
class OpenDictPropertiesEnabler