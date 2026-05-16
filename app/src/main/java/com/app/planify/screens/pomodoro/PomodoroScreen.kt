package com.app.planify.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlCard
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
