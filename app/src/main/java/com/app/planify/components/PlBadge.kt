package com.app.planify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun PlBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = PlColors.Primary,
    contentColor: Color = PlColors.OnPrimary
) {
    Box(
        modifier = modifier
            .background(containerColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = PlSpacing.sm, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = PlTypography.labelSmall,
            color = containerColor
        )
    }
}

