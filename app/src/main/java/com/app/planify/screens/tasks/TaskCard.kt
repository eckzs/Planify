package com.app.planify.screens.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.app.planify.api.models.Task
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
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.sm),
        contentPadding = PaddingValues(
            start = PlSpacing.md,
            end = PlSpacing.md,
            top = PlSpacing.sm,
            bottom = 80.dp
        )
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onEditClick = { onTaskClick(task.id) },
                onDeleteClick = { taskToDelete = task },
                onPomodoroClick = onPomodoroClick,
                onToggleCompletion = onToggleCompletion
            )
        }
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Eliminar tarea", style = PlTypography.titleMedium) },
            text = { Text("¿Eliminar \"${task.title}\"?", style = PlTypography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTask(task.id)
                    taskToDelete = null
                }) {
                    Text("Eliminar", color = PlColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPomodoroClick: (String) -> Unit,
    onToggleCompletion: (Task) -> Unit
) {
    val accentColor = priorityColor(task.priority)
    val contentAlpha = if (task.completed) 0.5f else 1f
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PlColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(PlSpacing.md),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlCircleCheck(
                checked = task.completed,
                accentColor = accentColor,
                onCheckedChange = { onToggleCompletion(task) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(contentAlpha)
            ) {
                Text(
                    text = task.title,
                    style = PlTypography.bodyMedium,
                    color = PlColors.TextMain,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
                ) {
                    PriorityDot(color = accentColor)
                    Text(
                        text = task.priority,
                        style = PlTypography.labelSmall,
                        color = PlColors.TextHint
                    )
                    if (task.date.isNotBlank()) {
                        Text("·", style = PlTypography.labelSmall, color = PlColors.TextHint)
                        Text(task.date, style = PlTypography.labelSmall, color = PlColors.TextHint)
                    }
                }
            }

            if (!task.completed) {
                IconButton(
                    onClick = { onPomodoroClick(task.id) },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Iniciar Pomodoro",
                        tint = PlColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Opciones",
                        tint = PlColors.TextHint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar", style = PlTypography.bodyMedium) },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar", style = PlTypography.bodyMedium, color = PlColors.Error) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Delete, contentDescription = null, tint = PlColors.Error, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlCircleCheck(
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (checked) accentColor else Color.Transparent,
        animationSpec = tween(200),
        label = "checkBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) accentColor else PlColors.TextHint.copy(alpha = 0.4f),
        animationSpec = tween(200),
        label = "checkBorder"
    )

    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.5.dp, borderColor, CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCheckedChange() },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun PriorityDot(color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun priorityColor(priority: String): Color = when (priority.lowercase()) {
    "alta", "high"    -> PlColors.Error
    "media", "medium" -> Color(0xFFE5960B)
    else              -> PlColors.Primary
}
