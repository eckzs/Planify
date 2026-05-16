package com.app.planify.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlCard
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToTasks: () -> Unit = {},
    onNavigateToPomodoro: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(PlSpacing.lg)
    ) {
        HomeHeader(userName = viewModel.userName)

        Spacer(Modifier.height(PlSpacing.lg))

        // --- Analytics Section ---
        Text(
            text = "Tu Progreso (últimos 7 días)",
            style = PlTypography.titleMedium,
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(PlSpacing.md))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.md)
        ) {
            ChartCard(
                title = "Pomodoros",
                data = viewModel.dailyPomodoros,
                modifier = Modifier.weight(1f)
            )
            ChartCard(
                title = "Tareas",
                data = viewModel.dailyTasks,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(PlSpacing.lg))

        PendingTasksCard(count = viewModel.pendingTasksCount)
        Spacer(Modifier.height(PlSpacing.md))
        RecentTasksCard(tasks = viewModel.recentTasks)
        
        Spacer(Modifier.height(PlSpacing.lg))
        Text("Acceso rápido", style = PlTypography.titleMedium, color = PlColors.TextMain)
        Spacer(Modifier.height(PlSpacing.sm))
        QuickAccessRow(
            onNavigateToTasks = onNavigateToTasks,
            onNavigateToPomodoro = onNavigateToPomodoro
        )
        Spacer(Modifier.height(PlSpacing.xl))
    }
}

@Composable
private fun HomeHeader(userName: String) {
    Text(
        text = "¡Hola, $userName!",
        style = PlTypography.headlineMedium,
        color = PlColors.TextMain
    )
    Text(
        text = "¿Qué estudias hoy?",
        style = PlTypography.bodyMedium,
        color = PlColors.TextHint
    )
}

@Composable
private fun PendingTasksCard(count: Int) {
    PlCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tareas pendientes", style = PlTypography.labelMedium, color = PlColors.TextHint)
                Spacer(Modifier.height(PlSpacing.xs))
                Text("$count tareas", style = PlTypography.headlineMedium, color = PlColors.TextMain)
            }
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = PlColors.Primary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun RecentTasksCard(tasks: List<String>) {
    PlCard(modifier = Modifier.fillMaxWidth()) {
        Text("Recientes", style = PlTypography.labelMedium, color = PlColors.TextHint)
        Spacer(Modifier.height(PlSpacing.sm))
        if (tasks.isEmpty()) {
            Text("No hay tareas pendientes", style = PlTypography.bodyMedium, color = PlColors.TextHint)
        }
        tasks.forEach { title ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = PlSpacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = PlColors.TextHint,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "  $title",
                    style = PlTypography.bodyMedium,
                    color = PlColors.TextMain
                )
            }
        }
    }
}

@Composable
private fun QuickAccessRow(
    onNavigateToTasks: () -> Unit,
    onNavigateToPomodoro: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.md)
    ) {
        PlCard(
            modifier = Modifier.weight(1f),
            onClick = onNavigateToTasks
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = PlColors.Primary, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(PlSpacing.xs))
                Text("Tareas", style = PlTypography.labelMedium, color = PlColors.TextMain)
            }
        }
        PlCard(
            modifier = Modifier.weight(1f),
            onClick = onNavigateToPomodoro
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Timer, contentDescription = null, tint = PlColors.Primary, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(PlSpacing.xs))
                Text("Pomodoro", style = PlTypography.labelMedium, color = PlColors.TextMain)
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    data: List<DailyMetric>,
    modifier: Modifier = Modifier
) {
    PlCard(modifier = modifier) {
        Column(modifier = Modifier.padding(PlSpacing.sm)) {
            Text(
                text = title,
                style = PlTypography.labelSmall,
                color = PlColors.TextHint,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(PlSpacing.sm))
            
            SimpleBarChart(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}

@Composable
private fun SimpleBarChart(
    data: List<DailyMetric>,
    modifier: Modifier = Modifier
) {
    val primaryColor = PlColors.Primary
    val textHintColor = PlColors.TextHint

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val maxCount = data.maxOf { it.count }.coerceAtLeast(1)
        val barWidth = size.width / (data.size * 1.5f)
        val spaceBetween = (size.width - (barWidth * data.size)) / (data.size + 1)

        data.forEachIndexed { index, metric ->
            val barHeight = (metric.count.toFloat() / maxCount.toFloat()) * (size.height * 0.7f)
            val x = spaceBetween + index * (barWidth + spaceBetween)
            val y = size.height - barHeight - 20.dp.toPx()

            // Draw Bar
            drawRect(
                color = primaryColor.copy(alpha = if (metric.count > 0) 1f else 0.1f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            // Draw Date (Day/Month)
            drawContext.canvas.nativeCanvas.drawText(
                metric.date,
                x + barWidth / 2,
                size.height - 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 8.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            
            // Draw Count
            if (metric.count > 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    metric.count.toString(),
                    x + barWidth / 2,
                    y - 5.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 8.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}
