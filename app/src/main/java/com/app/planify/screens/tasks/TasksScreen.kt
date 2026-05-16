package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlErrorMessage
import com.app.planify.components.PlFab
import com.app.planify.components.PlLoader
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography
import java.time.LocalDate

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = viewModel(),
    onNavigateToAdd: () -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToPomodoro: (String) -> Unit = {}
) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TasksHeader(
                selectedDate = viewModel.selectedDate,
                dates = viewModel.dates,
                onDateSelected = viewModel::onDateSelected
            )
            when (state) {
                is TasksState.Loading -> PlLoader()
                is TasksState.Error   -> PlErrorMessage(state.message)
                is TasksState.Success -> TasksList(
                    tasks = state.tasks,
                    onTaskClick = onNavigateToEdit,
                    onDeleteTask = viewModel::deleteTask,
                    onPomodoroClick = onNavigateToPomodoro,
                    onToggleCompletion = viewModel::toggleTaskCompletion
                )
            }
        }

        PlFab(
            onClick = onNavigateToAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(PlSpacing.lg)
        )
    }
}

@Composable
private fun TasksHeader(
    selectedDate: LocalDate,
    dates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PlSpacing.md)
    ) {
        Text(
            "Mis Tareas",
            style = PlTypography.headlineMedium,
            color = PlColors.TextMain,
            modifier = Modifier.padding(horizontal = PlSpacing.lg)
        )
        Spacer(Modifier.height(PlSpacing.md))

        DateSelector(
            selectedDate = selectedDate,
            dates = dates,
            onDateSelected = onDateSelected
        )
    }
}
