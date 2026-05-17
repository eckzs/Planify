package com.app.planify.logic.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object PomodoroState {
    var isRunning by mutableStateOf(false)
    var activeTaskId by mutableStateOf<String?>(null)
}
