package com.app.planify.api.models

import com.app.planify.constants.PomodoroConstants
import com.google.firebase.Timestamp

data class ActivePomodoro(
    val userId: String,
    val taskId: String,
    val mode: String,
    val cycleNumber: Int,
    val startedAt: Timestamp,
    val endsAt: Timestamp,
    val paused: Boolean,
    val completed: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            PomodoroConstants.FIELD_USER_ID to userId,
            PomodoroConstants.FIELD_TASK_ID to taskId,
            PomodoroConstants.FIELD_MODE to mode,
            PomodoroConstants.FIELD_CYCLE_NUMBER to cycleNumber,
            PomodoroConstants.FIELD_STARTED_AT to startedAt,
            PomodoroConstants.FIELD_ENDS_AT to endsAt,
            PomodoroConstants.FIELD_PAUSED to paused,
            PomodoroConstants.FIELD_COMPLETED to completed
        )
    }
}

data class PomodoroSession(
    val userId: String,
    val taskId: String,
    val mode: String,
    val cycleNumber: Int,
    val startedAt: Timestamp,
    val endedAt: Timestamp,
    val duration: Double,
    val completed: Boolean
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            PomodoroConstants.FIELD_USER_ID to userId,
            PomodoroConstants.FIELD_TASK_ID to taskId,
            PomodoroConstants.FIELD_MODE to mode,
            PomodoroConstants.FIELD_CYCLE_NUMBER to cycleNumber,
            PomodoroConstants.FIELD_STARTED_AT to startedAt,
            PomodoroConstants.FIELD_ENDED_AT to endedAt,
            PomodoroConstants.FIELD_DURATION to duration,
            PomodoroConstants.FIELD_COMPLETED to completed
        )
    }
}
