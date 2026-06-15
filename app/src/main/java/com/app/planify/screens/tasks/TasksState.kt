package com.app.planify.screens.tasks

import com.app.planify.api.models.Task

sealed class TasksState {
    object Loading : TasksState()
    data class Success(val tasks: List<Task>) : TasksState()
    data class Error(val message: String) : TasksState()
}
