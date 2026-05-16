package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.lazy.rememberLazyListState

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

@Composable
private fun DateSelector(
    selectedDate: LocalDate,
    dates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialIndex = remember(dates) {
        val index = dates.indexOf(LocalDate.now())
        if (index != -1) index else 0
    }
    
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(initialIndex) {
        if (initialIndex > 0) {
            listState.scrollToItem(initialIndex)
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = PlSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        items(
            items = dates,
            key = { it.toString() }
        ) { date ->
            DateItem(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).uppercase()
    val dayNumber = date.dayOfMonth.toString()

    Column(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PlColors.Primary else PlColors.Surface)
            .clickable { onClick() }
            .padding(vertical = PlSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayName,
            style = PlTypography.labelSmall,
            color = if (isSelected) PlColors.OnPrimary else PlColors.TextHint,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(PlSpacing.xs))
        Text(
            text = dayNumber,
            style = PlTypography.titleMedium,
            color = if (isSelected) PlColors.OnPrimary else PlColors.TextMain,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TasksList(
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
private fun TaskCard(
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
