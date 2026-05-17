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
import com.app.planify.logic.utils.PomodoroState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel() {

    private val tasksRepository = TasksRepository()
    private val pomodoroRepository = PomodoroRepository()
    private var timerJob: Job? = null

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

    fun loadTaskById(taskId: String) {
        if (PomodoroState.isRunning && PomodoroState.activeTaskId != taskId) {
            stopPomodoro()
        }

        viewModelScope.launch {
            tasksRepository.getTask(taskId)
                .onSuccess { task ->
                    associatedTask = task
                    if (!isRunning && !isPaused) resetTimer()
                }
                .onFailure {
                    errorMessage = it.message ?: "No se pudo cargar la tarea"
                }
        }
    }

    fun onTotalCyclesChange(value: String) {
        val cycles = value.toIntOrNull() ?: return
        totalCycles = cycles.coerceIn(1, 12)
    }

    fun startPomodoro() {
        val task = associatedTask ?: return
        cycleNumber = 1
        startPhase(PomodoroConstants.MODE_FOCUS)
    }

    fun pausePomodoro() {
        timerJob?.cancel()
        isRunning = false
        isPaused = true
        PomodoroState.isRunning = false

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
        PomodoroState.isRunning = true
        PomodoroState.activeTaskId = task.id

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
            while (isRunning) {
                val now = System.currentTimeMillis()
                val remaining = ((phaseEndsAtMillis - now) / 1000).toInt().coerceAtLeast(0)
                remainingSeconds = remaining
                progress = remaining.toFloat() / totalSeconds.toFloat()

                if (remaining <= 0) {
                    completeCurrentPhase()
                    break
                }
                delay(1000)
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
        PomodoroState.isRunning = false
        PomodoroState.activeTaskId = null
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
