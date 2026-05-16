package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Course
import com.app.planify.components.PlButton
import com.app.planify.components.PlInput
import com.app.planify.constants.TaskConstants
import com.app.planify.screens.courses.CoursesState
import com.app.planify.screens.courses.CoursesViewModel
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
    coursesViewModel: CoursesViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val isEditing = taskId != null
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTaskForEdit(taskId)
        } else {
            viewModel.prepareNewTask()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .verticalScroll(rememberScrollState())
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

        CourseDropdown(
            coursesViewModel = coursesViewModel,
            selectedCourseId = viewModel.courseId,
            onCourseChange = viewModel::onCourseChange
        )

        Spacer(Modifier.height(PlSpacing.md))

        PriorityDropdown(
            value = viewModel.priority,
            onValueChange = viewModel::onPriorityChange
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlInput(
            value = viewModel.tags,
            onValueChange = viewModel::onTagsChange,
            label = "Etiquetas (separadas por comas)"
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlInput(
            value = viewModel.evidenceUrl,
            onValueChange = viewModel::onEvidenceUrlChange,
            label = "Enlace a evidencia (URL)"
        )

        Spacer(Modifier.height(PlSpacing.md))

        OutlinedTextField(
            value = viewModel.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
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
                    viewModel.createTask(onSuccess = onNavigateBack)
                } else {
                    viewModel.updateTask(taskId = taskId, onSuccess = onNavigateBack)
                }
            }
        )
        
        Spacer(Modifier.height(PlSpacing.xl))
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
private fun CourseDropdown(
    coursesViewModel: CoursesViewModel,
    selectedCourseId: String?,
    onCourseChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val courses = (coursesViewModel.state as? CoursesState.Success)?.courses ?: emptyList()
    val selectedCourseName = courses.find { it.id == selectedCourseId }?.name ?: "Ninguno"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCourseName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Curso / Proyecto") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Ninguno") },
                onClick = {
                    onCourseChange(null)
                    expanded = false
                }
            )
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.name) },
                    onClick = {
                        onCourseChange(course.id)
                        expanded = false
                    }
                )
            }
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
