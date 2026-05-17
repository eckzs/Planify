package com.app.planify.screens.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.planify.api.models.Flashcard
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

private val FrontColor = Color(0xFFD4A017)
private val FrontColorBg = Color(0x1AD4A017)
private val BackColor = Color(0xFF4CAF50)
private val BackColorBg = Color(0x1A4CAF50)

@Composable
fun CardSide(text: String, label: String, modifier: Modifier = Modifier) {
    val isFront = label == "Pregunta"
    val accentColor = if (isFront) FrontColor else BackColor
    val accentBg = if (isFront) FrontColorBg else BackColorBg
    val icon = if (isFront) Icons.Outlined.HelpOutline else Icons.Outlined.Lightbulb

    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PlColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.dp, accentColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PlSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(accentBg)
                    .padding(horizontal = PlSpacing.md, vertical = PlSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PlSpacing.xs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    style = PlTypography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(PlSpacing.lg))
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
