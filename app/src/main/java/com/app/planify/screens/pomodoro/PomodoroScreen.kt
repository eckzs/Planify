package com.app.planify.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlCard
import com.app.planify.logic.utils.htmlToAnnotatedString
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun PomodoroScreen(
    taskId: String? = null,
    viewModel: PomodoroViewModel = viewModel()
) {
    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTaskById(taskId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .padding(PlSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            text = viewModel.associatedTask?.title ?: "Pomodoro",
            style = PlTypography.headlineMedium,
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(PlSpacing.sm))
        Text(
            text = modeText(viewModel.mode),
            style = PlTypography.bodyLarge,
            color = PlColors.TextHint
        )

        Spacer(Modifier.height(PlSpacing.xl))

        CircularTimer(
            progress = viewModel.progress,
            remainingTime = formatTime(viewModel.remainingSeconds)
        )

        Spacer(Modifier.height(PlSpacing.xl))

        PomodoroControls(viewModel = viewModel)

        viewModel.errorMessage?.let { message ->
            Spacer(Modifier.height(PlSpacing.md))
            Text(message, style = PlTypography.bodyMedium, color = PlColors.Error)
        }

        Spacer(Modifier.weight(1f))

        val notes = viewModel.associatedTask?.notes
        if (!notes.isNullOrBlank()) {
            val styledNotes = remember(notes) { htmlToAnnotatedString(notes) }
            if (styledNotes.isNotBlank()) {
                PlCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Notas",
                        style = PlTypography.labelMedium,
                        color = PlColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(PlSpacing.xs))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(PlSpacing.xl * 3)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = styledNotes,
                            style = PlTypography.bodyMedium,
                            color = PlColors.TextHint
                        )
                    }
                }
            }
        }
    }
}
