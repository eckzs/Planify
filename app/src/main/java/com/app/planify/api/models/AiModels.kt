package com.app.planify.api.models

data class ChatMessage(
    val role: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class GeneratedFlashcard(
    val front: String,
    val back: String
)
