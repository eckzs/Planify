package com.app.planify.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Task
import com.app.planify.components.PlCard
import com.app.planify.constants.PomodoroConstants
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun PomodoroScreen(
    taskId: String? = null,
    viewModel: PomodoroViewModel = viewModel()
) {
    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTaskById(taskId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(PlSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(PlSpacing.md))
        
        // --- Timer Section ---
        Text(
            text = viewModel.associatedTask?.title ?: "Pomodoro",
            style = PlTypography.headlineMedium,
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = modeText(viewModel.mode),
            style = PlTypography.bodyLarge,
            color = PlColors.TextHint
        )

        Spacer(Modifier.height(PlSpacing.lg))

        CircularTimer(
            progress = viewModel.progress,
            remainingTime = formatTime(viewModel.remainingSeconds)
        )

        Spacer(Modifier.height(PlSpacing.lg))

        PomodoroControls(viewModel = viewModel)

        Spacer(Modifier.height(PlSpacing.xl))

        // --- Dashboard Section ---
        PlCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(PlSpacing.md)) {
                Text(
                    text = "Mis Tareas",
                    style = PlTypography.titleLarge,
                    color = PlColors.TextMain,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(PlSpacing.md))
                
                // Filter Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
                ) {
                    FilterChip(
                        text = "Hoy",
                        isSelected = viewModel.filterMode == PomodoroFilter.TODAY,
                        onClick = { viewModel.onFilterModeChange(PomodoroFilter.TODAY) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "Semana",
                        isSelected = viewModel.filterMode == PomodoroFilter.WEEK,
                        onClick = { viewModel.onFilterModeChange(PomodoroFilter.WEEK) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(Modifier.height(PlSpacing.md))
                
                // Tasks List
                if (viewModel.filteredTasks.isEmpty()) {
                    Text(
                        text = "No hay tareas para este filtro",
                        style = PlTypography.bodyMedium,
                        color = PlColors.TextHint,
                        modifier = Modifier.padding(vertical = PlSpacing.md)
                    )
                } else {
                    viewModel.filteredTasks.forEach { task ->
                        DashboardTaskItem(
                            task = task,
                            isSelected = viewModel.associatedTask?.id == task.id,
                            onClick = { viewModel.selectTaskFromDashboard(task) }
                        )
                        Spacer(Modifier.height(PlSpacing.sm))
                    }
                }
            }
        }

        viewModel.errorMessage?.let { message ->
            Spacer(Modifier.height(PlSpacing.md))
            Text(message, style = PlTypography.bodyMedium, color = PlColors.Error)
        }
        
        Spacer(Modifier.height(PlSpacing.xl))
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PlColors.Primary else PlColors.Background)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = PlTypography.labelLarge,
            color = if (isSelected) PlColors.OnPrimary else PlColors.TextHint,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DashboardTaskItem(
    task: Task,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PlColors.Primary.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(PlSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Outlined.PlayCircleOutline else Icons.Outlined.Timer,
            contentDescription = null,
            tint = if (isSelected) PlColors.Primary else PlColors.TextHint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(PlSpacing.sm))
        Column {
            Text(
                text = task.title,
                style = PlTypography.bodyLarge,
                color = if (isSelected) PlColors.Primary else PlColors.TextMain,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = task.date,
                style = PlTypography.labelSmall,
                color = PlColors.TextHint
            )
        }
    }
}

@Composable
private fun CircularTimer(
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
private fun PomodoroControls(viewModel: PomodoroViewModel) {
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

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun modeText(mode: String): String {
    return when (mode) {
        PomodoroConstants.MODE_BREAK -> "Descanso"
        PomodoroConstants.MODE_LONG_BREAK -> "Descanso largo"
        else -> "Enfoque"
    }
}
