package com.app.planify.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.planify.components.PlInput
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun CourseFormDialog(
    viewModel: CoursesViewModel,
    onDismiss: () -> Unit
) {
    val isEditing = viewModel.editingCourseId != null
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Curso" else "Nuevo Curso") },
        text = {
            Column {
                PlInput(
                    value = viewModel.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre del curso"
                )
                Spacer(Modifier.height(PlSpacing.md))
                PlInput(
                    value = viewModel.teacherName,
                    onValueChange = viewModel::onTeacherNameChange,
                    label = "Nombre del profesor"
                )
                Spacer(Modifier.height(PlSpacing.md))
                Text("Color", style = PlTypography.labelLarge)
                Spacer(Modifier.height(PlSpacing.sm))
                ColorPicker(selectedColor = viewModel.color, onColorSelect = viewModel::onColorChange)
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.saveCourse { onDismiss() } }) {
                Text(if (isEditing) "Guardar" else "Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteCourseDialog(
    courseName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar curso") },
        text = { Text("¿Seguro que deseas eliminar \"$courseName\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = PlColors.Error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ColorPicker(selectedColor: String, onColorSelect: (String) -> Unit) {
    val colors = listOf("#4285F4", "#EA4335", "#FBBC05", "#34A853", "#8E24AA", "#F06292")
    Row(horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)) {
        colors.forEach { colorStr ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(colorStr)))
                    .clickable { onColorSelect(colorStr) }
                    .then(
                        if (selectedColor == colorStr)
                            Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        else Modifier
                    )
            )
        }
    }
}
