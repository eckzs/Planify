package com.app.planify.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app.planify.components.PlCard
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun PendingTasksCard(count: Int, onClick: () -> Unit = {}) {
    PlCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
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
fun RecentTasksCard(tasks: List<String>, onClick: () -> Unit = {}) {
    PlCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
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
fun AiCtaCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PlColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(PlSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = PlColors.OnPrimary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Asistente IA", style = PlTypography.titleMedium, color = PlColors.OnPrimary)
                Text(
                    "Pregúntame o genera flashcards al instante",
                    style = PlTypography.bodyMedium,
                    color = PlColors.OnPrimary
                )
            }
        }
    }
}

@Composable
fun QuickAccessRow(
    onNavigateToTasks: () -> Unit,
    onNavigateToPomodoro: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToAi: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(PlSpacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.md)
        ) {
            QuickAccessCard("Tareas", Icons.Outlined.CheckCircle, onNavigateToTasks, Modifier.weight(1f))
            QuickAccessCard("Pomodoro", Icons.Outlined.Timer, onNavigateToPomodoro, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.md)
        ) {
            QuickAccessCard("Cursos", Icons.AutoMirrored.Outlined.MenuBook, onNavigateToCourses, Modifier.weight(1f))
            QuickAccessCard("Asistente", Icons.Outlined.AutoAwesome, onNavigateToAi, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickAccessCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlCard(modifier = modifier, onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(icon, contentDescription = null, tint = PlColors.Primary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(PlSpacing.xs))
            Text(label, style = PlTypography.labelMedium, color = PlColors.TextMain)
        }
    }
}
