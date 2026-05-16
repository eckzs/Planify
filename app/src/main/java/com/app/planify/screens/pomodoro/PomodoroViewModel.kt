package com.app.planify.screens.pomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.models.Task
import com.app.planify.api.services.PomodoroRepository
import com.app.planify.api.services.TasksRepository
import com.app.planify.constants.PomodoroConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek

class PomodoroViewModel : ViewModel() {

    private val tasksRepository = TasksRepository()
    private val pomodoroRepository = PomodoroRepository()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var timerJob: Job? = null

    var tasks by mutableStateOf(emptyList<Task>())
        private set

    var filteredTasks by mutableStateOf(emptyList<Task>())
        private set

    var filterMode by mutableStateOf(PomodoroFilter.TODAY)
        private set

    var selectedTask by mutableStateOf<Task?>(null)
        private set

    var associatedTask by mutableStateOf<Task?>(null)
        private set

    var mode by mutableStateOf(PomodoroConstants.MODE_FOCUS)
        private set

    var cycleNumber by mutableStateOf(1)
        private set

    var totalCycles by mutableStateOf(1)
        private set

    var remainingSeconds by mutableStateOf(focusSeconds())
        private set

    var isRunning by mutableStateOf(false)
        private set

    var isPaused by mutableStateOf(false)
        private set

    var progress by mutableStateOf(1f)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var phaseStartedAtMillis: Long = 0L
    private var phaseEndsAtMillis: Long = 0L

    fun onFilterModeChange(mode: PomodoroFilter) {
        filterMode = mode
        applyFilter()
    }

    private fun applyFilter() {
        val today = LocalDate.now()
        filteredTasks = when (filterMode) {
            PomodoroFilter.TODAY -> {
                val todayStr = today.format(dateFormatter)
                tasks.filter { it.date == todayStr }
            }
            PomodoroFilter.WEEK -> {
                val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                tasks.filter {
                    try {
                        val taskDate = LocalDate.parse(it.date, dateFormatter)
                        !taskDate.isBefore(startOfWeek) && !taskDate.isAfter(endOfWeek)
                    } catch (e: Exception) { false }
                }
            }
        }
    }

    fun selectTaskFromDashboard(task: Task) {
        associatedTask = task
        resetTimer()
    }

    fun loadTaskById(taskId: String) {
        viewModelScope.launch {
            tasksRepository.getTask(taskId)
                .onSuccess { task ->
                    associatedTask = task
                    resetTimer()
                }
                .onFailure {
                    errorMessage = it.message ?: "No se pudo cargar la tarea"
                }
        }
    }

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            tasksRepository.getTasks()
                .onSuccess { taskList ->
                    tasks = taskList
                    applyFilter()
                    selectedTask = selectedTask ?: taskList.firstOrNull()
                }
                .onFailure {
                    errorMessage = it.message ?: "No se pudieron cargar las tareas"
                }
        }
    }

    private fun loadMetrics() {
        viewModelScope.launch {
            pomodoroRepository.getSessions()
                .onSuccess { sessions: List<com.app.planify.api.models.PomodoroSession> ->
                    // Proceso de métricas si fuera necesario en este ViewModel
                }
        }
    }

    fun onTaskSelected(task: Task) {
        selectedTask = task
    }

    fun associateSelectedTask() {
        associatedTask = selectedTask
        errorMessage = null
    }

    fun onTotalCyclesChange(value: String) {
        val cycles = value.toIntOrNull() ?: return
        totalCycles = cycles.coerceIn(1, 12)
    }

    fun startPomodoro() {
        val task = associatedTask

        if (task == null) {
            errorMessage = "Selecciona y asocia una tarea primero"
            return
        }

        cycleNumber = 1
        startPhase(PomodoroConstants.MODE_FOCUS)
    }

    fun pausePomodoro() {
        timerJob?.cancel()
        isRunning = false
        isPaused = true

        viewModelScope.launch {
            pomodoroRepository.updatePaused(true)
                .onFailure {
                    errorMessage = it.message ?: "No se pudo pausar el pomodoro"
                }
        }
    }

    fun resumePomodoro() {
        val task = associatedTask ?: return
        phaseStartedAtMillis = System.currentTimeMillis()
        phaseEndsAtMillis = phaseStartedAtMillis + remainingSeconds * 1000L
        isRunning = true
        isPaused = false

        viewModelScope.launch {
            pomodoroRepository.saveActivePomodoro(
                taskId = task.id,
                mode = mode,
                cycleNumber = cycleNumber,
                startedAtMillis = phaseStartedAtMillis,
                endsAtMillis = phaseEndsAtMillis,
                paused = false
            )
        }

        startTimer()
    }

    fun stopPomodoro() {
        val task = associatedTask
        val endedAtMillis = System.currentTimeMillis()

        timerJob?.cancel()
        isRunning = false
        isPaused = false

        if (task != null && phaseStartedAtMillis > 0L) {
            viewModelScope.launch {
                // Guardamos solo la sesión en la colección 'pomodoro'
                pomodoroRepository.savePomodoroSession(
                    taskId = task.id,
                    mode = mode,
                    cycleNumber = cycleNumber,
                    startedAtMillis = phaseStartedAtMillis,
                    endedAtMillis = endedAtMillis,
                    completed = false
                )
            }
        }

        resetTimer()
    }

    private fun startPhase(newMode: String) {
        val task = associatedTask ?: return
        mode = newMode
        remainingSeconds = secondsForMode(newMode)
        progress = 1f
        phaseStartedAtMillis = System.currentTimeMillis()
        phaseEndsAtMillis = phaseStartedAtMillis + remainingSeconds * 1000L
        isRunning = true
        isPaused = false
        errorMessage = null

        viewModelScope.launch {
            pomodoroRepository.saveActivePomodoro(
                taskId = task.id,
                mode = mode,
                cycleNumber = cycleNumber,
                startedAtMillis = phaseStartedAtMillis,
                endsAtMillis = phaseEndsAtMillis,
                paused = false,
                completed = false
            )
                .onFailure {
                    errorMessage = it.message ?: "No se pudo guardar el pomodoro"
                }
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        val totalSeconds = secondsForMode(mode)
        timerJob = viewModelScope.launch {
            while (remainingSeconds > 0 && isRunning) {
                delay(1000)
                remainingSeconds--
                progress = remainingSeconds.toFloat() / totalSeconds.toFloat()
            }

            if (remainingSeconds == 0 && isRunning) {
                completeCurrentPhase()
            }
        }
    }

    private fun completeCurrentPhase() {
        val task = associatedTask ?: return
        val finishedMode = mode
        val finishedCycle = cycleNumber
        val endedAtMillis = System.currentTimeMillis()

        viewModelScope.launch {
            pomodoroRepository.savePomodoroSession(
                taskId = task.id,
                mode = finishedMode,
                cycleNumber = finishedCycle,
                startedAtMillis = phaseStartedAtMillis,
                endedAtMillis = endedAtMillis,
                completed = true
            )

            if (finishedMode == PomodoroConstants.MODE_FOCUS) {
                if (finishedCycle >= totalCycles) {
                    pomodoroRepository.saveActivePomodoro(
                        taskId = task.id,
                        mode = PomodoroConstants.MODE_FOCUS,
                        cycleNumber = finishedCycle,
                        startedAtMillis = phaseStartedAtMillis,
                        endsAtMillis = endedAtMillis,
                        paused = false,
                        completed = true
                    )
                    isRunning = false
                    isPaused = false
                    resetTimer()
                } else {
                    startPhase(PomodoroConstants.MODE_BREAK)
                }
            } else {
                cycleNumber++
                startPhase(PomodoroConstants.MODE_FOCUS)
            }
        }
    }

    private fun resetTimer() {
        mode = PomodoroConstants.MODE_FOCUS
        cycleNumber = 1
        remainingSeconds = focusSeconds()
        phaseStartedAtMillis = 0L
        phaseEndsAtMillis = 0L
    }

    private fun secondsForMode(mode: String): Int {
        return when (mode) {
            PomodoroConstants.MODE_BREAK -> PomodoroConstants.BREAK_MINUTES * 60
            PomodoroConstants.MODE_LONG_BREAK -> PomodoroConstants.LONG_BREAK_MINUTES * 60
            else -> focusSeconds()
        }
    }

    private fun focusSeconds(): Int {
        return PomodoroConstants.FOCUS_MINUTES * 60
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
