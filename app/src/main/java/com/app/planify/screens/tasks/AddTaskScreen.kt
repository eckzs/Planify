package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlButton
import com.app.planify.components.PlInput
import com.app.planify.constants.TaskConstants
import com.app.planify.screens.courses.CoursesState
import com.app.planify.screens.courses.CoursesViewModel
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
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
    val richTextState = rememberRichTextState()
    var notesLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (taskId != null) viewModel.loadTaskForEdit(taskId)
        else viewModel.prepareNewTask()
    }

    LaunchedEffect(viewModel.notes) {
        if (!notesLoaded && viewModel.notes.isNotBlank()) {
            richTextState.setHtml(viewModel.notes)
            notesLoaded = true
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlSpacing.sm, vertical = PlSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Volver", tint = PlColors.TextMain)
                }
                Text(
                    text = if (isEditing) "Editar tarea" else "Nueva tarea",
                    style = PlTypography.headlineMedium,
                    color = PlColors.TextMain
                )
            }
        },
        containerColor = PlColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PlSpacing.lg)
        ) {
            Spacer(Modifier.height(PlSpacing.sm))

            OutlinedTextField(
                value = viewModel.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = {
                    Text(
                        "Título de la tarea",
                        style = PlTypography.headlineMedium,
                        color = PlColors.TextHint
                    )
                },
                textStyle = PlTypography.headlineMedium.copy(color = PlColors.TextMain),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlColors.Primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = PlColors.Container,
                    unfocusedContainerColor = PlColors.Container
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(PlSpacing.lg))

            FormSectionLabel("Detalles")
            Spacer(Modifier.height(PlSpacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)) {
                Box(modifier = Modifier.weight(1f)) {
                    CourseDropdown(
                        coursesViewModel = coursesViewModel,
                        selectedCourseId = viewModel.courseId,
                        onCourseChange = viewModel::onCourseChange
                    )
                }
                DateChip(
                    date = viewModel.date,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(PlSpacing.lg))

            FormSectionLabel("Prioridad")
            Spacer(Modifier.height(PlSpacing.sm))
            PrioritySelector(
                selected = viewModel.priority,
                onSelect = viewModel::onPriorityChange
            )

            Spacer(Modifier.height(PlSpacing.lg))

            FormSectionLabel("Descripción")
            Spacer(Modifier.height(PlSpacing.sm))
            RichEditorToolbar(state = richTextState)
            RichTextEditor(
                state = richTextState,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 140.dp)
                    .background(
                        PlColors.Container,
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    ),
                placeholder = {
                    Text("Agrega detalles, lista de pasos, links...", color = PlColors.TextHint)
                },
                colors = RichTextEditorDefaults.richTextEditorColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(PlSpacing.lg))

            FormSectionLabel("Enlace de evidencia")
            Spacer(Modifier.height(PlSpacing.sm))
            PlInput(
                value = viewModel.evidenceUrl,
                onValueChange = viewModel::onEvidenceUrlChange,
                label = "URL"
            )

            Spacer(Modifier.height(PlSpacing.xl))

            PlButton(
                text = if (isEditing) "Guardar cambios" else "Guardar tarea",
                enabled = viewModel.title.isNotBlank(),
                onClick = {
                    viewModel.onNotesChange(richTextState.toHtml())
                    if (taskId == null) viewModel.createTask(onSuccess = onNavigateBack)
                    else viewModel.updateTask(taskId = taskId, onSuccess = onNavigateBack)
                }
            )

            Spacer(Modifier.height(PlSpacing.xl))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDateChange(formatDate(millis))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FormSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = PlTypography.labelSmall,
        color = PlColors.TextHint,
        letterSpacing = 1.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateChip(date: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = PlColors.Container),
        border = androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PlSpacing.md, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.xs)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = PlColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = date.ifBlank { "Fecha" },
                style = PlTypography.bodyMedium,
                color = if (date.isBlank()) PlColors.TextHint else PlColors.TextMain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PrioritySelector(selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)) {
        listOf(
            TaskConstants.PRIORITY_HIGH,
            TaskConstants.PRIORITY_MEDIUM,
            TaskConstants.PRIORITY_LOW
        ).forEach { priority ->
            FilterChip(
                selected = selected == priority,
                onClick = { onSelect(priority) },
                label = { Text(priority, style = PlTypography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PlColors.Primary,
                    selectedLabelColor = PlColors.OnPrimary
                )
            )
        }
    }
}

@Composable
private fun RichEditorToolbar(state: com.mohamedrejeb.richeditor.model.RichTextState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PlColors.Container.copy(alpha = 0.7f),
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(horizontal = PlSpacing.xs, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FormatButton(label = "B", fontWeight = FontWeight.Bold) {
            state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
        }
        FormatButton(label = "I", fontStyle = FontStyle.Italic) {
            state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
        }
        FormatButton(label = "U", textDecoration = TextDecoration.Underline) {
            state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
        }
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(PlColors.TextHint.copy(alpha = 0.3f))
        )
        Spacer(Modifier.width(PlSpacing.xs))
        IconButton(
            onClick = { state.toggleUnorderedList() },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.List,
                contentDescription = "Lista",
                tint = PlColors.TextHint,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(
            onClick = { state.toggleOrderedList() },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatListNumbered,
                contentDescription = "Lista numerada",
                tint = PlColors.TextHint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FormatButton(
    label: String,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
    ) {
        Text(
            text = label,
            style = PlTypography.labelLarge.copy(
                fontWeight = fontWeight ?: FontWeight.Normal,
                fontStyle = fontStyle ?: FontStyle.Normal,
                textDecoration = textDecoration
            ),
            color = PlColors.TextHint
        )
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
            label = { Text("Curso") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PlColors.Primary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = PlColors.Container,
                unfocusedContainerColor = PlColors.Container
            ),
            shape = RoundedCornerShape(12.dp),
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
                onClick = { onCourseChange(null); expanded = false }
            )
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.name) },
                    onClick = { onCourseChange(course.id); expanded = false }
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
