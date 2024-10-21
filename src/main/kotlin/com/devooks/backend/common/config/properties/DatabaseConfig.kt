package com.devooks.backend.common.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.r2dbc")
data class DatabaseConfig(
    val url: String,
    val driver: String,
    val protocol: String,
    val host: String,
    val port: String,
    val database: String,
    val username: String,
    val password: String,
)
