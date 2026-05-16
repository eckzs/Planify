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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TasksViewModel : ViewModel() {

    private val tasksRepository = TasksRepository()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var state by mutableStateOf<TasksState>(TasksState.Loading)
        private set

    var title by mutableStateOf("")
        private set

    var date by mutableStateOf("")
        private set

    var priority by mutableStateOf(TaskConstants.PRIORITY_MEDIUM)
        private set

    var courseId by mutableStateOf<String?>(null)
        private set

    var tags by mutableStateOf("")
        private set

    var notes by mutableStateOf("")
        private set

    var evidenceUrl by mutableStateOf("")
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    val dates: List<LocalDate> = run {
        val startOfYear = LocalDate.now().withDayOfYear(1)
        val endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear())
        val daysCount = java.time.temporal.ChronoUnit.DAYS.between(startOfYear, endOfYear)
        (0..daysCount).map { startOfYear.plusDays(it) }
    }

    init {
        loadTasks()
    }

    fun onDateSelected(newDate: LocalDate) {
        selectedDate = newDate
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            state = TasksState.Loading

            tasksRepository.getTasks()
                .onSuccess { allTasks ->
                    val filteredTasks = allTasks.filter { task ->
                        task.date == selectedDate.format(dateFormatter)
                    }
                    state = TasksState.Success(filteredTasks)
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudieron cargar las tareas")
                }
        }
    }

    fun onTitleChange(value: String) { title = value }
    fun onDateChange(value: String) { date = value }
    fun onPriorityChange(value: String) { priority = value }
    fun onCourseChange(value: String?) { courseId = value }
    fun onTagsChange(value: String) { tags = value }
    fun onNotesChange(value: String) { notes = value }
    fun onEvidenceUrlChange(value: String) { evidenceUrl = value }

    fun loadTaskForEdit(taskId: String) {
        viewModelScope.launch {
            tasksRepository.getTask(taskId)
                .onSuccess { task ->
                    title = task.title
                    date = task.date
                    priority = task.priority.ifBlank { TaskConstants.PRIORITY_MEDIUM }
                    courseId = task.courseId
                    tags = task.tags.joinToString(", ")
                    notes = task.notes
                    evidenceUrl = task.evidenceUrl ?: ""
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo cargar la tarea")
                }
        }
    }

    fun createTask(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (title.isBlank()) {
                state = TasksState.Error("El titulo es obligatorio")
                return@launch
            }

            val tagsList = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }

            tasksRepository.createTask(
                title = title, 
                date = date, 
                priority = priority,
                courseId = courseId,
                tags = tagsList,
                evidenceUrl = evidenceUrl.takeIf { it.isNotBlank() },
                notes = notes
            )
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

    fun updateTask(taskId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (title.isBlank()) {
                state = TasksState.Error("El titulo es obligatorio")
                return@launch
            }

            val tagsList = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }

            tasksRepository.updateTask(
                taskId = taskId, 
                title = title, 
                date = date, 
                priority = priority,
                courseId = courseId,
                tags = tagsList,
                evidenceUrl = evidenceUrl.takeIf { it.isNotBlank() },
                notes = notes
            )
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

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            tasksRepository.toggleTaskCompletion(task.id, !task.completed)
                .onSuccess { loadTasks() }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo actualizar la tarea")
                }
        }
    }

    fun prepareNewTask() {
        clearForm()
        date = selectedDate.format(dateFormatter)
    }

    private fun clearForm() {
        title = ""
        date = ""
        priority = TaskConstants.PRIORITY_MEDIUM
        courseId = null
        tags = ""
        notes = ""
        evidenceUrl = ""
    }
}

sealed class TasksState {
    object Loading : TasksState()
    data class Success(val tasks: List<Task>) : TasksState()
    data class Error(val message: String) : TasksState()
}
