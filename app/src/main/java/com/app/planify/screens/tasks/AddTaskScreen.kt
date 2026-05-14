package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlButton
import com.app.planify.components.PlInput
import com.app.planify.constants.TaskConstants
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskId: String? = null,
    viewModel: TasksViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val isEditing = taskId != null
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTaskForEdit(taskId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .padding(horizontal = PlSpacing.lg)
    ) {
        Spacer(Modifier.height(PlSpacing.lg))

        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Volver", tint = PlColors.TextMain)
        }

        Spacer(Modifier.height(PlSpacing.lg))

        Text(
            text = if (isEditing) "Editar tarea" else "Nueva tarea",
            style = PlTypography.headlineMedium,
            color = PlColors.TextMain
        )
        Spacer(Modifier.height(PlSpacing.xl))

        PlInput(
            value = viewModel.title,
            onValueChange = viewModel::onTitleChange,
            label = "Titulo"
        )

        Spacer(Modifier.height(PlSpacing.md))

        PriorityDropdown(
            value = viewModel.priority,
            onValueChange = viewModel::onPriorityChange
        )

        Spacer(Modifier.height(PlSpacing.xl))

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = viewModel.date.ifBlank { "Seleccionar fecha" },
                style = PlTypography.bodyLarge
            )
        }

        Spacer(Modifier.height(PlSpacing.md))

        PlButton(
            text = if (isEditing) "Guardar cambios" else "Guardar",
            enabled = viewModel.title.isNotBlank(),
            onClick = {
                if (taskId == null) {
                    viewModel.createTask(
                        title = viewModel.title,
                        date = viewModel.date,
                        priority = viewModel.priority.ifBlank { TaskConstants.PRIORITY_MEDIUM },
                        onSuccess = onNavigateBack
                    )
                } else {
                    viewModel.updateTask(
                        taskId = taskId,
                        title = viewModel.title,
                        date = viewModel.date,
                        priority = viewModel.priority.ifBlank { TaskConstants.PRIORITY_MEDIUM },
                        onSuccess = onNavigateBack
                    )
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onDateChange(formatDate(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityDropdown(
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        TaskConstants.PRIORITY_HIGH,
        TaskConstants.PRIORITY_MEDIUM,
        TaskConstants.PRIORITY_LOW
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Prioridad") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}
