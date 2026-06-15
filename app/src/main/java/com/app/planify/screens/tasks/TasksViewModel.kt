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

    var notes by mutableStateOf("")
        private set

    var evidenceUrl by mutableStateOf("")
        private set

    var isEditLoading by mutableStateOf(false)
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var showAllTasks by mutableStateOf(false)
        private set

    val selectedDateFormatted: String
        get() = selectedDate.format(dateFormatter)

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

    fun onShowAllTasksChange(value: Boolean) {
        showAllTasks = value
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            state = TasksState.Loading

            tasksRepository.getTasks()
                .onSuccess { allTasks ->
                    val filteredTasks = if (showAllTasks) {
                        allTasks
                    } else {
                        allTasks.filter { task ->
                            task.date == selectedDate.format(dateFormatter)
                        }
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
    fun onNotesChange(value: String) { notes = value }
    fun onEvidenceUrlChange(value: String) { evidenceUrl = value }

    fun loadTaskForEdit(taskId: String) {
        isEditLoading = true
        viewModelScope.launch {
            tasksRepository.getTask(taskId)
                .onSuccess { task ->
                    title = task.title
                    date = task.date
                    priority = task.priority.ifBlank { TaskConstants.PRIORITY_MEDIUM }
                    courseId = task.courseId
                    notes = task.notes
                    evidenceUrl = task.evidenceUrl ?: ""
                }
                .onFailure {
                    state = TasksState.Error(it.message ?: "No se pudo cargar la tarea")
                }
            isEditLoading = false
        }
    }

    fun createTask(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (title.isBlank()) {
                state = TasksState.Error("El titulo es obligatorio")
                return@launch
            }

            tasksRepository.createTask(
                title = title,
                date = date,
                priority = priority,
                courseId = courseId,
                tags = emptyList(),
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

            tasksRepository.updateTask(
                taskId = taskId,
                title = title,
                date = date,
                priority = priority,
                courseId = courseId,
                tags = emptyList(),
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
        val current = state
        if (current is TasksState.Success) {
            state = TasksState.Success(current.tasks.filter { it.id != taskId })
        }

        viewModelScope.launch {
            tasksRepository.deleteTask(taskId)
                .onFailure {
                    state = current
                }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val current = state
        if (current is TasksState.Success) {
            val updated = current.tasks.map {
                if (it.id == task.id) it.copy(completed = !it.completed) else it
            }
            state = TasksState.Success(updated)
        }

        viewModelScope.launch {
            tasksRepository.toggleTaskCompletion(task.id, !task.completed)
                .onFailure {
                    state = current
                }
        }
    }

    fun prepareNewTask(initialDate: String? = null) {
        clearForm()
        date = initialDate?.takeIf { it.isNotBlank() } ?: selectedDate.format(dateFormatter)
    }

    private fun clearForm() {
        title = ""
        date = ""
        priority = TaskConstants.PRIORITY_MEDIUM
        courseId = null
        notes = ""
        evidenceUrl = ""
    }
}
