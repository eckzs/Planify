package com.app.planify.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlTypography

@Composable
fun PomodoroScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pomodoro — próximamente",
            style = PlTypography.bodyMedium,
            color = PlColors.TextHint
        )
    }
}
