package com.app.planify.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.planify.constants.PomodoroConstants
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun CircularTimer(
    progress: Float,
    remainingTime: String
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "timerProgress")
    val primaryColor = PlColors.Primary
    val hintColor = PlColors.TextHint

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(modifier = Modifier.size(280.dp)) {
            // Background Circle
            drawCircle(
                color = hintColor.copy(alpha = 0.1f),
                style = Stroke(width = 12.dp.toPx())
            )
            // Progress Arc
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = remainingTime,
            style = PlTypography.headlineLarge.copy(fontSize = 48.sp),
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PomodoroControls(viewModel: PomodoroViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.isRunning) {
            IconButton(
                onClick = viewModel::pausePomodoro,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = PlColors.Primary.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Outlined.Pause, contentDescription = "Pausar", tint = PlColors.Primary, modifier = Modifier.size(32.dp))
            }
        } else {
            IconButton(
                onClick = {
                    if (viewModel.isPaused) viewModel.resumePomodoro() else viewModel.startPomodoro()
                },
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = PlColors.Primary)
            ) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = "Iniciar", tint = PlColors.OnPrimary, modifier = Modifier.size(48.dp))
            }
        }

        if (viewModel.isRunning || viewModel.isPaused) {
            Spacer(Modifier.size(PlSpacing.lg))
            IconButton(
                onClick = viewModel::stopPomodoro,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = PlColors.Error.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Outlined.Stop, contentDescription = "Detener", tint = PlColors.Error, modifier = Modifier.size(32.dp))
            }
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

fun modeText(mode: String): String {
    return when (mode) {
        PomodoroConstants.MODE_BREAK -> "Descanso"
        PomodoroConstants.MODE_LONG_BREAK -> "Descanso largo"
        else -> "Enfoque"
    }
}
