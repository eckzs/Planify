package com.app.planify.screens.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.services.FlashcardsRepository
import com.app.planify.logic.utils.applySpacedRepetition
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class FlashcardsViewModel : ViewModel() {
    private val flashcardsRepository = FlashcardsRepository()

    var state by mutableStateOf<FlashcardsState>(FlashcardsState.Loading)
        private set

    var currentCardIndex by mutableStateOf(0)
        private set

    var isFlipped by mutableStateOf(false)
        private set

    fun loadCards(courseId: String) {
        viewModelScope.launch {
            state = FlashcardsState.Loading
            flashcardsRepository.getCardsByCourse(courseId)
                .onSuccess { allCards ->
                    val nowSeconds = Timestamp.now().seconds
                    val dueCards = allCards.filter { it.nextReview.seconds <= nowSeconds }
                    state = FlashcardsState.Success(dueCards = dueCards, totalCards = allCards.size)
                    currentCardIndex = 0
                    isFlipped = false
                }
                .onFailure { state = FlashcardsState.Error(it.message ?: "Error al cargar tarjetas") }
        }
    }

    fun flipCard() {
        isFlipped = !isFlipped
    }

    // rating: 1 = Otra vez, 3 = Difícil, 4 = Bien
    fun onReviewResult(rating: Int) {
        val currentState = state as? FlashcardsState.Success ?: return
        val card = currentState.dueCards.getOrNull(currentCardIndex) ?: return
        val updatedCard = applySpacedRepetition(card, rating)

        viewModelScope.launch {
            flashcardsRepository.updateCard(updatedCard)

            if (currentCardIndex < currentState.dueCards.size - 1) {
                currentCardIndex++
                isFlipped = false
            } else {
                state = FlashcardsState.Finished
            }
        }
    }
}
