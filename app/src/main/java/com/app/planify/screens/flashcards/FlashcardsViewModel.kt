package com.app.planify.screens.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.Flashcard
import com.app.planify.api.services.FlashcardsRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.math.roundToInt

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

    private fun applySpacedRepetition(card: Flashcard, rating: Int): Flashcard {
        val newRepetitions: Int
        val newInterval: Int
        val newEaseFactor: Double

        if (rating >= 3) {
            newRepetitions = card.repetitions + 1
            newInterval = when (card.repetitions) {
                0 -> 1
                1 -> 6
                else -> (card.interval * card.easeFactor).roundToInt()
            }
            newEaseFactor = (card.easeFactor + 0.1 - (5 - rating) * (0.08 + (5 - rating) * 0.02))
                .coerceAtLeast(1.3)
        } else {
            newRepetitions = 0
            newInterval = 1
            newEaseFactor = card.easeFactor
        }

        val nextReviewInstant = LocalDate.now()
            .plusDays(newInterval.toLong())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return card.copy(
            repetitions = newRepetitions,
            interval = newInterval,
            easeFactor = newEaseFactor,
            nextReview = Timestamp(Date.from(nextReviewInstant))
        )
    }
}

sealed class FlashcardsState {
    object Loading : FlashcardsState()
    data class Success(val dueCards: List<Flashcard>, val totalCards: Int) : FlashcardsState()
    data class Error(val message: String) : FlashcardsState()
    object Finished : FlashcardsState()
}
