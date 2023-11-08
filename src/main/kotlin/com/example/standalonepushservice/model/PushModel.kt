package com.example.standalonepushservice.model

data class PushModel(
    val title: String,
    val body: String,
    val tokenList: List<String>,
    val topic: String?,
    val channel: ChannelType,
) enum class ChannelType {
    FIREBASE,
    APNS,
}
