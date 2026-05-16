package com.app.planify.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import com.app.planify.api.models.Task
import com.app.planify.components.PlCard
import com.app.planify.components.PlPriorityBadge
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun TasksList(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onPomodoroClick: (String) -> Unit,
    onToggleCompletion: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.sm),
        contentPadding = PaddingValues(
            horizontal = PlSpacing.lg,
            vertical = PlSpacing.sm
        )
    ) {
        items(tasks) { task ->
            TaskCard(
                task = task,
                onTaskClick = onTaskClick,
                onDeleteTask = onDeleteTask,
                onPomodoroClick = onPomodoroClick,
                onToggleCompletion = onToggleCompletion
            )
        }
        item { Spacer(Modifier.height(PlSpacing.xl)) }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onTaskClick: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onPomodoroClick: (String) -> Unit,
    onToggleCompletion: (Task) -> Unit
) {
    PlCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onTaskClick(task.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onToggleCompletion(task) }
                )
                Spacer(Modifier.width(PlSpacing.xs))
                Column {
                    Text(
                        text = task.title,
                        style = PlTypography.titleMedium,
                        color = if (task.completed) PlColors.TextHint else PlColors.TextMain,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = task.date,
                        style = PlTypography.labelSmall,
                        color = PlColors.TextHint
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!task.completed) {
                    IconButton(onClick = { onPomodoroClick(task.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.PlayCircleOutline,
                            contentDescription = "Iniciar Pomodoro",
                            tint = PlColors.Primary
                        )
                    }
                }
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
