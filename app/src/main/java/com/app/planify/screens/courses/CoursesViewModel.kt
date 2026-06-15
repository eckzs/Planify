package com.app.planify.screens.courses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.Course
import com.app.planify.api.services.CoursesRepository
import kotlinx.coroutines.launch

class CoursesViewModel : ViewModel() {
    private val coursesRepository = CoursesRepository()

    var state by mutableStateOf<CoursesState>(CoursesState.Loading)
        private set

    var name by mutableStateOf("")
        private set

    var teacherName by mutableStateOf("")
        private set

    var color by mutableStateOf("#4285F4") // Color por defecto
        private set

    var editingCourseId by mutableStateOf<String?>(null)
        private set

    init {
        loadCourses()
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onTeacherNameChange(value: String) {
        teacherName = value
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

    fun startCreateCourse() {
        editingCourseId = null
        name = ""
        teacherName = ""
        color = "#4285F4"
    }

    fun startEditCourse(course: Course) {
        editingCourseId = course.id
        name = course.name
        teacherName = course.teacherName
        color = course.color.ifBlank { "#4285F4" }
    }

    fun saveCourse(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (name.isBlank()) return@launch
            val courseId = editingCourseId
            val result = if (courseId == null) {
                coursesRepository.createCourse(name, teacherName, color)
            } else {
                coursesRepository.updateCourse(courseId, name, teacherName, color)
            }
            result
                .onSuccess {
                    editingCourseId = null
                    name = ""
                    teacherName = ""
                    color = "#4285F4"
                    loadCourses()
                    onSuccess()
                }
                .onFailure { state = CoursesState.Error(it.message ?: "Error al guardar curso") }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            coursesRepository.deleteCourse(courseId)
                .onSuccess { loadCourses() }
                .onFailure { state = CoursesState.Error(it.message ?: "Error al eliminar curso") }
        }
    }
}
