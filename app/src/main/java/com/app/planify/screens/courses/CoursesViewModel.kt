package com.app.planify.screens.courses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.services.CoursesRepository
import kotlinx.coroutines.launch

class CoursesViewModel : ViewModel() {
    private val coursesRepository = CoursesRepository()

    var state by mutableStateOf<CoursesState>(CoursesState.Loading)
        private set

    var name by mutableStateOf("")
        private set

    var color by mutableStateOf("#4285F4") // Color por defecto
        private set

    init {
        loadCourses()
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onColorChange(value: String) {
        color = value
    }

    fun loadCourses() {
        viewModelScope.launch {
            state = CoursesState.Loading
            coursesRepository.getCourses()
                .onSuccess { state = CoursesState.Success(it) }
                .onFailure { state = CoursesState.Error(it.message ?: "Error al cargar cursos") }
        }
    }

    fun createCourse(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (name.isBlank()) return@launch
            coursesRepository.createCourse(name, color)
                .onSuccess {
                    name = ""
                    loadCourses()
                    onSuccess()
                }
                .onFailure { state = CoursesState.Error(it.message ?: "Error al crear curso") }
        }
    }
}
