package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Task
import com.app.planify.components.PlCard
import com.app.planify.components.PlErrorMessage
import com.app.planify.components.PlFab
import com.app.planify.components.PlLoader
import com.app.planify.components.PlPriorityBadge
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = viewModel(),
    onNavigateToAdd: () -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {}
) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TasksHeader()
            when (state) {
                is TasksState.Loading -> PlLoader()
                is TasksState.Error   -> PlErrorMessage(state.message)
                is TasksState.Success -> TasksList(
                    tasks = state.tasks,
                    onTaskClick = onNavigateToEdit,
                    onDeleteTask = viewModel::deleteTask
                )
            }
        }

        // TODO: navegar a AddTaskScreen cuando exista
        PlFab(
            onClick = onNavigateToAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(PlSpacing.lg)
        )
    }
}

@Composable
private fun TasksHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PlSpacing.lg, vertical = PlSpacing.md)
    ) {
        Text("Mis Tareas", style = PlTypography.headlineMedium, color = PlColors.TextMain)
        Text("Organiza tu semana", style = PlTypography.bodyMedium, color = PlColors.TextHint)
    }
}

@Composable
private fun TasksList(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.sm),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = PlSpacing.lg,
            vertical = PlSpacing.sm
        )
    ) {
        items(tasks) { task ->
            TaskCard(
                task = task,
                onTaskClick = onTaskClick,
                onDeleteTask = onDeleteTask
            )
        }
        // Spacer para que el FAB no tape la última tarea
        item { Spacer(Modifier.height(PlSpacing.xl)) }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onTaskClick: (String) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    PlCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onTaskClick(task.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = PlTypography.titleMedium,
                    color = PlColors.TextMain
                )
                Spacer(Modifier.height(PlSpacing.xs))
                Text(
                    text = task.date,
                    style = PlTypography.labelSmall,
                    color = PlColors.TextHint
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlPriorityBadge(priority = task.priority)
                IconButton(onClick = { onDeleteTask(task.id) }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = PlColors.Error
                    )
                }
            }
        }
    }
}
