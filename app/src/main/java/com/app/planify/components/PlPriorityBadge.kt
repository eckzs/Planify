package com.app.planify.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.app.planify.ui.theme.PlColors

@Composable
fun PlPriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val color = when (priority.lowercase()) {
        "alta", "high"    -> PlColors.Error
        "media", "medium" -> Color(0xFFB45309)
        else              -> PlColors.Primary
    }
    PlBadge(text = priority, modifier = modifier, containerColor = color)
}
