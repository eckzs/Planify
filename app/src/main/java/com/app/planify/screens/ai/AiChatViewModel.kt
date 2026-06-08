package com.app.planify.screens.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.ChatMessage
import com.app.planify.api.models.Course
import com.app.planify.api.services.AiRepository
import com.app.planify.api.services.CoursesRepository
import com.app.planify.api.services.FlashcardsRepository
import kotlinx.coroutines.launch

class AiChatViewModel : ViewModel() {

    private val aiRepository = AiRepository()
    private val coursesRepository = CoursesRepository()
    private val flashcardsRepository = FlashcardsRepository()

    // ── Chat state ────────────────────────────────────────────────────────────

    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set

    var inputText by mutableStateOf("")
        private set

    var uiState by mutableStateOf<AiChatUiState>(AiChatUiState.Idle)
        private set

    // ── Courses (needed for both options A and B) ─────────────────────────────

    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    // ── Opción A: quick-action sheet ──────────────────────────────────────────

    var showGenerateSheet by mutableStateOf(false)
        private set

    var sheetSelectedCourse by mutableStateOf<Course?>(null)
        private set

    var sheetTopic by mutableStateOf("")
        private set

    var sheetCount by mutableStateOf(5)
        private set

    var isGeneratingFromSheet by mutableStateOf(false)
        private set

    // ─────────────────────────────────────────────────────────────────────────

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            coursesRepository.getCourses()
                .onSuccess { list ->
                    courses = list
                    if (sheetSelectedCourse == null) sheetSelectedCourse = list.firstOrNull()
                }
        }
    }

    // ── Chat ──────────────────────────────────────────────────────────────────

    fun onInputChange(text: String) { inputText = text }

    fun onSend() {
        val text = inputText.trim()
        if (text.isBlank() || uiState is AiChatUiState.Loading) return

        val historySnapshot = messages.toList()
        messages = messages + ChatMessage(role = "user", text = text)
        inputText = ""
        uiState = AiChatUiState.Loading

        viewModelScope.launch {
            // Opción B: usa sendChatMessage con function calling habilitado
            aiRepository.sendChatMessage(
                history = historySnapshot,
                newMessage = text,
                courseNames = courses.map { it.name }
            ) { name, args ->
                when (name) {
                    "generate_flashcards" -> executeGenerateFlashcards(args)
                    else -> "Función no disponible: $name"
                }
            }
                .onSuccess { response ->
                    messages = messages + ChatMessage(role = "model", text = response)
                    uiState = AiChatUiState.Idle
                }
                .onFailure { error ->
                    uiState = AiChatUiState.Error(error.message ?: "Error")
                }
        }
    }

    // Ejecuta la función que Gemini solicitó: genera y guarda las tarjetas
    private suspend fun executeGenerateFlashcards(args: Map<String, String>): String {
        val topic = args["topic"]?.takeIf { it.isNotBlank() }
            ?: return "No especificaste el tema de las flashcards."
        val count = args["count"]?.toIntOrNull()?.coerceIn(1, 20) ?: 5
        val courseName = args["course_name"]?.takeIf { it.isNotBlank() }
            ?: return "No especificaste el curso."

        val course = courses.find { c ->
            c.name.lowercase().contains(courseName.lowercase()) ||
            courseName.lowercase().contains(c.name.lowercase())
        } ?: return "No encontré el curso \"$courseName\". " +
            "Tus cursos son: ${courses.joinToString(", ") { it.name }}."

        return aiRepository.generateFlashcards(topic, count).fold(
            onSuccess = { cards ->
                for (card in cards) {
                    flashcardsRepository.createCard(course.id, card.front, card.back)
                }
                "Guardé ${cards.size} flashcards sobre \"$topic\" en ${course.name}."
            },
            onFailure = { "Error al generar las flashcards: ${it.message}" }
        )
    }

    fun dismissError() { uiState = AiChatUiState.Idle }

    // ── Opción A: sheet handlers ──────────────────────────────────────────────

    fun openGenerateSheet() {
        if (sheetSelectedCourse == null) sheetSelectedCourse = courses.firstOrNull()
        showGenerateSheet = true
    }

    fun closeGenerateSheet() { showGenerateSheet = false }

    fun onSheetCourseChange(course: Course) { sheetSelectedCourse = course }

    fun onSheetTopicChange(text: String) { sheetTopic = text }

    fun onSheetCountChange(count: Int) { sheetCount = count }

    fun onGenerateFromSheet() {
        val course = sheetSelectedCourse ?: return
        val topic = sheetTopic.trim().takeIf { it.isNotBlank() } ?: return
        isGeneratingFromSheet = true

        viewModelScope.launch {
            aiRepository.generateFlashcards(topic, sheetCount)
                .onSuccess { cards ->
                    for (card in cards) {
                        flashcardsRepository.createCard(course.id, card.front, card.back)
                    }
                    messages = messages + ChatMessage(
                        role = "model",
                        text = "Guardé ${cards.size} flashcards sobre \"$topic\" en ${course.name}."
                    )
                    isGeneratingFromSheet = false
                    showGenerateSheet = false
                    sheetTopic = ""
                }
                .onFailure { err ->
                    uiState = AiChatUiState.Error(err.message ?: "Error al generar")
                    isGeneratingFromSheet = false
                }
        }
    }
}
