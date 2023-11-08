package com.example.standalonepushservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "apns")
class ApnsConfig(
    val teamId: String,
    val keyId: String,
    val topic: String,
    val authKeyPath: String,
)
