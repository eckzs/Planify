package com.app.planify.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.planify.api.models.Task
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PlColors.Primary else PlColors.Background)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = PlTypography.labelLarge,
            color = if (isSelected) PlColors.OnPrimary else PlColors.TextHint,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DashboardTaskItem(
    task: Task,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PlColors.Primary.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(PlSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Outlined.PlayCircleOutline else Icons.Outlined.Timer,
            contentDescription = null,
            tint = if (isSelected) PlColors.Primary else PlColors.TextHint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(PlSpacing.sm))
        Column {
            Text(
                text = task.title,
                style = PlTypography.bodyLarge,
                color = if (isSelected) PlColors.Primary else PlColors.TextMain,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = task.date,
                style = PlTypography.labelSmall,
                color = PlColors.TextHint
            )
        }
    }
}
