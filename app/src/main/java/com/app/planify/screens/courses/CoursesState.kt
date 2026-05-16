package com.app.planify.screens.courses

import com.app.planify.api.models.Course

sealed class CoursesState {
    object Loading : CoursesState()
    data class Success(val courses: List<Course>) : CoursesState()
    data class Error(val message: String) : CoursesState()
}
