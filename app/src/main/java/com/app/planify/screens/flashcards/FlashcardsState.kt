package com.app.planify.screens.flashcards

import com.app.planify.api.models.Flashcard

sealed class FlashcardsState {
    object Loading : FlashcardsState()
    data class Success(val dueCards: List<Flashcard>, val totalCards: Int) : FlashcardsState()
    data class Error(val message: String) : FlashcardsState()
    object Finished : FlashcardsState()
}
