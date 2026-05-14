package com.app.planify.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Task
import com.app.planify.components.PlButton
import com.app.planify.components.PlCard
import com.app.planify.components.PlInput
import com.app.planify.constants.PomodoroConstants
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .padding(PlSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pomodoro", style = PlTypography.headlineMedium, color = PlColors.TextMain)
        Text("Enfoca una tarea", style = PlTypography.bodyMedium, color = PlColors.TextHint)

        Spacer(Modifier.height(PlSpacing.xl))

        TaskSelector(
            tasks = viewModel.tasks,
            selectedTask = viewModel.selectedTask,
            onTaskSelected = viewModel::onTaskSelected
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlInput(
            value = viewModel.totalCycles.toString(),
            onValueChange = viewModel::onTotalCyclesChange,
            label = "Numero de ciclos"
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlButton(
            text = "Asociar tarea",
            enabled = viewModel.selectedTask != null && !viewModel.isRunning,
            onClick = viewModel::associateSelectedTask
        )

        Spacer(Modifier.height(PlSpacing.lg))

        PlCard(modifier = Modifier.fillMaxWidth()) {
            Text("Tarea asociada", style = PlTypography.labelMedium, color = PlColors.TextHint)
            Spacer(Modifier.height(PlSpacing.xs))
            Text(
                text = viewModel.associatedTask?.title ?: "Ninguna tarea asociada",
                style = PlTypography.titleMedium,
                color = PlColors.TextMain
            )
            Spacer(Modifier.height(PlSpacing.sm))
            Text(
                text = "Modo: ${modeText(viewModel.mode)}",
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint
            )
            Text(
                text = "Ciclo ${viewModel.cycleNumber} de ${viewModel.totalCycles}",
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint
            )
        }

        Spacer(Modifier.height(PlSpacing.xl))

        Text(
            text = formatTime(viewModel.remainingSeconds),
            style = PlTypography.headlineLarge,
            color = PlColors.Primary
        )

        Spacer(Modifier.height(PlSpacing.xl))

        PomodoroButtons(viewModel = viewModel)

        viewModel.errorMessage?.let { message ->
            Spacer(Modifier.height(PlSpacing.md))
            Text(message, style = PlTypography.bodyMedium, color = PlColors.Error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskSelector(
    tasks: List<Task>,
    selectedTask: Task?,
    onTaskSelected: (Task) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedTask?.title ?: "No hay tareas",
            onValueChange = {},
            readOnly = true,
            enabled = tasks.isNotEmpty(),
            label = { Text("Elegir tarea por nombre") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tasks.forEach { task ->
                DropdownMenuItem(
                    text = { Text(task.title) },
                    onClick = {
                        onTaskSelected(task)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PomodoroButtons(viewModel: PomodoroViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        when {
            viewModel.isRunning -> {
                PlButton(text = "Pausar", onClick = viewModel::pausePomodoro)
                PlButton(text = "Terminar", onClick = viewModel::stopPomodoro)
            }

            viewModel.isPaused -> {
                PlButton(text = "Continuar", onClick = viewModel::resumePomodoro)
                PlButton(text = "Terminar", onClick = viewModel::stopPomodoro)
            }

            else -> {
                PlButton(
                    text = "Iniciar",
                    enabled = viewModel.associatedTask != null,
                    onClick = viewModel::startPomodoro
                )
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
