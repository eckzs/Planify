package com.app.planify.screens.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.GeneratedFlashcard
import com.app.planify.api.services.AiRepository
import com.app.planify.api.services.FlashcardsRepository
import kotlinx.coroutines.launch

import com.app.planify.api.services.CoursesRepository
import kotlinx.coroutines.launch

class GenerateFlashcardsViewModel : ViewModel() {

    private val aiRepository = AiRepository()
    private val flashcardsRepository = FlashcardsRepository()
    private val coursesRepository = CoursesRepository()

    var courseName by mutableStateOf("")
        private set

    var inputText by mutableStateOf("")
        private set

    var selectedCount by mutableStateOf(5)
        private set

    var generatedCards by mutableStateOf<List<GeneratedFlashcard>>(emptyList())
        private set

    var isGenerating by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            coursesRepository.getCourseById(courseId).onSuccess {
                courseName = it.name
            }
        }
    }

    fun onInputChange(text: String) { inputText = text }

    fun onCountChange(count: Int) { selectedCount = count }

    fun onGenerate() {
        if (inputText.isBlank()) {
            error = "Ingresa un tema o texto para generar tarjetas"
            return
        }
        isGenerating = true
        error = null
        generatedCards = emptyList()

        viewModelScope.launch {
            aiRepository.generateFlashcards(courseName, inputText, selectedCount)
                .onSuccess { cards ->
                    generatedCards = cards
                    isGenerating = false
                }
                .onFailure { err ->
                    error = err.message
                    isGenerating = false
                }
        }
    }

    fun onSaveAll(courseId: String, onSuccess: () -> Unit) {
        if (generatedCards.isEmpty()) return
        isSaving = true
        error = null

        viewModelScope.launch {
            var allOk = true
            for (card in generatedCards) {
                flashcardsRepository.createCard(courseId, card.front, card.back)
                    .onFailure { allOk = false }
            }
            isSaving = false
            if (allOk) {
                onSuccess()
            } else {
                error = "Algunas tarjetas no pudieron guardarse"
            }
        }
    }

    fun dismissError() { error = null }
}
