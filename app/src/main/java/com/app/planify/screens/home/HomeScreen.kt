package com.app.planify.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToTasks: () -> Unit = {},
    onNavigateToPomodoro: () -> Unit = {},
    onNavigateToCourses: () -> Unit = {},
    onNavigateToAi: () -> Unit = {}
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
            text = "Esta semana",
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

        PendingTasksCard(count = viewModel.pendingTasksCount, onClick = onNavigateToTasks)
        Spacer(Modifier.height(PlSpacing.md))
        RecentTasksCard(tasks = viewModel.recentTasks, onClick = onNavigateToTasks)

        Spacer(Modifier.height(PlSpacing.lg))
        AiCtaCard(onClick = onNavigateToAi)

        Spacer(Modifier.height(PlSpacing.lg))
        Text("Acceso rápido", style = PlTypography.titleMedium, color = PlColors.TextMain)
        Spacer(Modifier.height(PlSpacing.sm))
        QuickAccessRow(
            onNavigateToTasks = onNavigateToTasks,
            onNavigateToPomodoro = onNavigateToPomodoro,
            onNavigateToCourses = onNavigateToCourses,
            onNavigateToAi = onNavigateToAi
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
