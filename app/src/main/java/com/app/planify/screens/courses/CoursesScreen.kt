package com.app.planify.screens.courses

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Course
import com.app.planify.components.PlErrorMessage
import com.app.planify.components.PlLoader
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = viewModel(),
    onNavigateToCourseDetail: (String) -> Unit = {}
) {
    var showFormDialog by remember { mutableStateOf(false) }
    var courseToDelete by remember { mutableStateOf<Course?>(null) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(PlSpacing.lg)) {
                Text(
                    "Mis Cursos",
                    style = PlTypography.headlineMedium,
                    color = PlColors.TextMain
                )
                Text(
                    "Toca un curso para estudiar sus flashcards",
                    style = PlTypography.bodyMedium,
                    color = PlColors.TextHint
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startCreateCourse()
                    showFormDialog = true
                },
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
                            onCourseClick = onNavigateToCourseDetail,
                            onEditCourse = { course ->
                                viewModel.startEditCourse(course)
                                showFormDialog = true
                            },
                            onDeleteCourse = { course -> courseToDelete = course }
                        )
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        CourseFormDialog(
            viewModel = viewModel,
            onDismiss = { showFormDialog = false }
        )
    }

    courseToDelete?.let { course ->
        DeleteCourseDialog(
            courseName = course.name,
            onConfirm = {
                viewModel.deleteCourse(course.id)
                courseToDelete = null
            },
            onDismiss = { courseToDelete = null }
        )
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
        Text("Crea un curso para generar y estudiar flashcards", style = PlTypography.bodyMedium, color = PlColors.TextHint)
    }
}
