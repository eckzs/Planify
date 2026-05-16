package com.app.planify.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Course
import com.app.planify.components.PlButton
import com.app.planify.components.PlCard
import com.app.planify.components.PlErrorMessage
import com.app.planify.components.PlInput
import com.app.planify.components.PlLoader
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = viewModel(),
    onNavigateToCourseDetail: (String) -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Text(
                "Mis Cursos",
                style = PlTypography.headlineMedium,
                color = PlColors.TextMain,
                modifier = Modifier.padding(PlSpacing.lg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PlColors.Primary,
                contentColor = PlColors.OnPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Curso")
            }
        },
        containerColor = PlColors.Background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = viewModel.state) {
                is CoursesState.Loading -> PlLoader()
                is CoursesState.Error -> PlErrorMessage(state.message)
                is CoursesState.Success -> {
                    if (state.courses.isEmpty()) {
                        EmptyCoursesState()
                    } else {
                        CoursesList(
                            courses = state.courses,
                            onCourseClick = onNavigateToCourseDetail
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCourseDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun CoursesList(
    courses: List<Course>,
    onCourseClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(PlSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.md)
    ) {
        items(courses) { course ->
            CourseCard(course = course, onClick = { onCourseClick(course.id) })
        }
    }
}

@Composable
private fun CourseCard(course: Course, onClick: () -> Unit) {
    PlCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(PlSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(course.color)))
            )
            Spacer(Modifier.width(PlSpacing.md))
            Text(
                text = course.name,
                style = PlTypography.titleMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyCoursesState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No tienes cursos creados", style = PlTypography.bodyLarge, color = PlColors.TextHint)
        Text("Crea uno para empezar a organizar tu estudio", style = PlTypography.bodyMedium, color = PlColors.TextHint)
    }
}

@Composable
private fun AddCourseDialog(
    viewModel: CoursesViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Curso") },
        text = {
            Column {
                PlInput(
                    value = viewModel.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre del curso"
                )
                Spacer(Modifier.height(PlSpacing.md))
                Text("Color", style = PlTypography.labelLarge)
                Spacer(Modifier.height(PlSpacing.sm))
                ColorPicker(selectedColor = viewModel.color, onColorSelect = viewModel::onColorChange)
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.createCourse { onDismiss() } }) {
                Text("Crear")
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
private fun ColorPicker(selectedColor: String, onColorSelect: (String) -> Unit) {
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
