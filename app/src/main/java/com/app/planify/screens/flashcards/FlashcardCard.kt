package com.app.planify.screens.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.planify.api.models.Flashcard
import com.app.planify.components.PlCard
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun StudyProgress(current: Int, total: Int) {
    Column(modifier = Modifier.padding(horizontal = PlSpacing.lg, vertical = PlSpacing.sm)) {
        Text(
            "$current / $total tarjetas",
            style = PlTypography.labelMedium,
            color = PlColors.TextHint
        )
        Spacer(Modifier.height(PlSpacing.xs))
        LinearProgressIndicator(
            progress = current.toFloat() / total.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = PlColors.Primary,
            trackColor = PlColors.Container
        )
    }
}

@Composable
fun StudyContent(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onResult: (Int) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "cardFlip"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PlSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { onFlip() }
        ) {
            if (rotation <= 90f) {
                CardSide(text = card.front, label = "Pregunta")
            } else {
                CardSide(
                    text = card.back,
                    label = "Respuesta",
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }

        Spacer(Modifier.height(PlSpacing.xl))

        if (isFlipped) {
            RatingButtons(onResult = onResult)
        } else {
            Text(
                "Toca la carta para ver la respuesta",
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint
            )
        }
    }
}

@Composable
fun CardSide(text: String, label: String, modifier: Modifier = Modifier) {
    PlCard(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PlSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = PlTypography.labelSmall,
                color = PlColors.Primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(PlSpacing.md))
            Text(
                text = text,
                style = PlTypography.headlineMedium,
                color = PlColors.TextMain,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
        }
    }
}
