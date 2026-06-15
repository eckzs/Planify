package com.app.planify.screens.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.Flashcard
import com.app.planify.api.services.AiRepository
import com.app.planify.api.services.FlashcardsRepository
import com.app.planify.logic.utils.applySpacedRepetition
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class FlashcardsViewModel : ViewModel() {
    private val flashcardsRepository = FlashcardsRepository()
    private val aiRepository = AiRepository()

    private var courseId: String = ""

    var state by mutableStateOf<FlashcardsState>(FlashcardsState.Loading)
        private set

    var currentCardIndex by mutableStateOf(0)
        private set

    var isFlipped by mutableStateOf(false)
        private set

    var allCards by mutableStateOf<List<Flashcard>>(emptyList())
        private set

    var showManageSheet by mutableStateOf(false)
        private set

    var editingCard by mutableStateOf<Flashcard?>(null)
        private set

    var editFront by mutableStateOf("")
        private set

    var editBack by mutableStateOf("")
        private set

    var deletingCard by mutableStateOf<Flashcard?>(null)
        private set

    var showExplanation by mutableStateOf(false)
        private set

    var isExplaining by mutableStateOf(false)
        private set

    var explanationText by mutableStateOf<String?>(null)
        private set

    var explanationError by mutableStateOf<String?>(null)
        private set

    fun loadCards(courseId: String) {
        this.courseId = courseId
        viewModelScope.launch {
            state = FlashcardsState.Loading
            flashcardsRepository.getCardsByCourse(courseId)
                .onSuccess { cards ->
                    val nowSeconds = Timestamp.now().seconds
                    val dueCards = cards.filter { it.nextReview.seconds <= nowSeconds }
                    allCards = cards
                    state = FlashcardsState.Success(dueCards = dueCards, totalCards = cards.size)
                    currentCardIndex = 0
                    isFlipped = false
                }
                .onFailure { state = FlashcardsState.Error(it.message ?: "Error al cargar tarjetas") }
        }
    }

    private fun reloadCards() {
        viewModelScope.launch {
            flashcardsRepository.getCardsByCourse(courseId)
                .onSuccess { cards ->
                    allCards = cards
                    val current = state as? FlashcardsState.Success ?: return@onSuccess
                    state = current.copy(totalCards = cards.size)
                }
        }
    }

    fun openManageSheet() {
        showManageSheet = true
    }

    fun dismissManageSheet() {
        showManageSheet = false
    }

    fun startEditCard(card: Flashcard) {
        editingCard = card
        editFront = card.front
        editBack = card.back
    }

    fun onEditFrontChange(value: String) {
        editFront = value
    }

    fun onEditBackChange(value: String) {
        editBack = value
    }

    fun cancelEditCard() {
        editingCard = null
    }

    fun saveEditCard() {
        val card = editingCard ?: return
        if (editFront.isBlank() || editBack.isBlank()) return
        viewModelScope.launch {
            flashcardsRepository.updateCardContent(card.id, editFront, editBack)
                .onSuccess {
                    editingCard = null
                    reloadCards()
                }
        }
    }

    fun askDeleteCard(card: Flashcard) {
        deletingCard = card
    }

    fun cancelDeleteCard() {
        deletingCard = null
    }

    fun confirmDeleteCard() {
        val card = deletingCard ?: return
        viewModelScope.launch {
            flashcardsRepository.deleteCard(card.id)
                .onSuccess {
                    deletingCard = null
                    reloadCards()
                }
        }
    }

    fun flipCard() {
        isFlipped = !isFlipped
    }

    fun onReviewResult(rating: Int) {
        val currentState = state as? FlashcardsState.Success ?: return
        val card = currentState.dueCards.getOrNull(currentCardIndex) ?: return
        val updatedCard = applySpacedRepetition(card, rating)

        viewModelScope.launch {
            flashcardsRepository.updateCard(updatedCard)

            if (currentCardIndex < currentState.dueCards.size - 1) {
                currentCardIndex++
                isFlipped = false
                dismissExplanation()
            } else {
                state = FlashcardsState.Finished
            }
        }
    }

    fun explainCurrentCard() {
        val currentState = state as? FlashcardsState.Success ?: return
        val card = currentState.dueCards.getOrNull(currentCardIndex) ?: return

        showExplanation = true
        isExplaining = true
        explanationText = null
        explanationError = null

        viewModelScope.launch {
            aiRepository.explainFlashcard(card.front, card.back)
                .onSuccess { text ->
                    explanationText = text
                    isExplaining = false
                }
                .onFailure { err ->
                    explanationError = err.message
                    isExplaining = false
                }
        }
    }

    fun dismissExplanation() {
        showExplanation = false
        isExplaining = false
        explanationText = null
        explanationError = null
    }
}
