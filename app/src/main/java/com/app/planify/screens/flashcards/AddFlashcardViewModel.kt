package com.app.planify.screens.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.services.FlashcardsRepository
import kotlinx.coroutines.launch

class AddFlashcardViewModel : ViewModel() {
    private val flashcardsRepository = FlashcardsRepository()

    var front by mutableStateOf("")
        private set

    var back by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onFrontChange(value: String) {
        front = value
        errorMessage = null
    }

    fun onBackChange(value: String) {
        back = value
        errorMessage = null
    }

    fun createCard(courseId: String, onSuccess: () -> Unit) {
        if (front.isBlank() || back.isBlank()) {
            errorMessage = "Ambos campos son obligatorios"
            return
        }
        viewModelScope.launch {
            isLoading = true
            flashcardsRepository.createCard(courseId, front, back)
                .onSuccess {
                    front = ""
                    back = ""
                    isLoading = false
                    onSuccess()
                }
                .onFailure {
                    isLoading = false
                    errorMessage = it.message ?: "Error al guardar la tarjeta"
                }
        }
    }
}
