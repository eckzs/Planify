package com.app.planify.screens.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.Task
import com.app.planify.api.services.TasksRepository
import com.app.planify.constants.TaskConstants
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {

    private val tasksRepository = TasksRepository()

    var state by mutableStateOf<TasksState>(TasksState.Loading)
        private set

    var title by mutableStateOf("")
        private set

    var date by mutableStateOf("")
        private set

    var priority by mutableStateOf(TaskConstants.PRIORITY_MEDIUM)
        private set

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            state = TasksState.Loading

            tasksRepository.getTasks()
                .onSuccess { tasks ->
                    state = TasksState.Success(tasks)
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudieron cargar las tareas")
                }
        }
    }

    fun onTitleChange(value: String) {
        title = value
    }

    fun onDateChange(value: String) {
        date = value
    }

    fun onPriorityChange(value: String) {
        priority = value
    }

    fun loadTaskForEdit(taskId: String) {
        viewModelScope.launch {
            tasksRepository.getTask(taskId)
                .onSuccess { task ->
                    title = task.title
                    date = task.date
                    priority = task.priority.ifBlank { TaskConstants.PRIORITY_MEDIUM }
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo cargar la tarea")
                }
        }
    }

    fun createTask(
        title: String,
        date: String,
        priority: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                state = TasksState.Error("El titulo es obligatorio")
                return@launch
            }

            tasksRepository.createTask(title, date, priority)
                .onSuccess {
                    clearForm()
                    loadTasks()
                    onSuccess()
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo crear la tarea")
                }
        }
    }

    fun updateTask(
        taskId: String,
        title: String,
        date: String,
        priority: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                state = TasksState.Error("El titulo es obligatorio")
                return@launch
            }

            tasksRepository.updateTask(taskId, title, date, priority)
                .onSuccess {
                    clearForm()
                    loadTasks()
                    onSuccess()
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo actualizar la tarea")
                }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            tasksRepository.deleteTask(taskId)
                .onSuccess { loadTasks() }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo eliminar la tarea")
                }
        }
    }

    private fun clearForm() {
        title = ""
        date = ""
        priority = TaskConstants.PRIORITY_MEDIUM
    }
}

sealed class TasksState {
    object Loading : TasksState()
    data class Success(val tasks: List<Task>) : TasksState()
    data class Error(val message: String) : TasksState()
}
