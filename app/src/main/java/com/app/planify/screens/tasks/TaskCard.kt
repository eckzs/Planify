package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.sm),
        contentPadding = PaddingValues(
            start = PlSpacing.md,
            end = PlSpacing.md,
            top = PlSpacing.sm,
            bottom = 80.dp
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
    val accentColor = priorityColor(task.priority)

    PlCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onTaskClick(task.id) }
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(if (task.completed) PlColors.TextHint.copy(alpha = 0.3f) else accentColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = PlSpacing.sm, end = PlSpacing.xs, top = PlSpacing.sm, bottom = PlSpacing.xs)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = task.title,
                        style = PlTypography.bodyMedium,
                        color = if (task.completed) PlColors.TextHint else PlColors.TextMain,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = { onToggleCompletion(task) },
                        modifier = Modifier.size(28.dp),
                        colors = CheckboxDefaults.colors(checkedColor = PlColors.Primary)
                    )
                }

                if (task.date.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = task.date,
                        style = PlTypography.labelSmall,
                        color = PlColors.TextHint
                    )
                }

                Spacer(Modifier.height(PlSpacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlPriorityBadge(priority = task.priority)
                    Row {
                        if (!task.completed) {
                            IconButton(
                                onClick = { onPomodoroClick(task.id) },
                                modifier = Modifier.size(28.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayCircleOutline,
                                    contentDescription = "Pomodoro",
                                    tint = PlColors.Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDeleteTask(task.id) },
                            modifier = Modifier.size(28.dp),
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Eliminar",
                                tint = PlColors.TextHint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun priorityColor(priority: String): Color = when (priority.lowercase()) {
    "alta", "high"    -> PlColors.Error
    "media", "medium" -> Color(0xFFB45309)
    else              -> PlColors.Primary
}
