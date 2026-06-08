package com.app.planify.screens.ai

sealed class AiChatUiState {
    object Idle : AiChatUiState()
    object Loading : AiChatUiState()
    data class Error(val message: String) : AiChatUiState()
}
