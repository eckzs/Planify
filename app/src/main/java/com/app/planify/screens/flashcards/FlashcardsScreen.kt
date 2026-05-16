package com.app.planify.screens.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Flashcard
import com.app.planify.components.PlButton
import com.app.planify.components.PlCard
import com.app.planify.components.PlErrorMessage
import com.app.planify.components.PlLoader
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun FlashcardsScreen(
    courseId: String,
    viewModel: FlashcardsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {}
) {
    LaunchedEffect(courseId) {
        viewModel.loadCards(courseId)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlSpacing.md, vertical = PlSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = PlColors.TextMain)
                }
                Text(
                    "Repaso",
                    style = PlTypography.headlineMedium,
                    color = PlColors.TextMain,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = PlColors.Primary,
                contentColor = PlColors.OnPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva tarjeta")
            }
        },
        containerColor = PlColors.Background
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = viewModel.state) {
                is FlashcardsState.Loading -> PlLoader()
                is FlashcardsState.Error -> PlErrorMessage(state.message)
                is FlashcardsState.Finished -> StudyFinishedState(onNavigateBack)
                is FlashcardsState.Success -> {
                    if (state.dueCards.isEmpty()) {
                        NoDueCardsState(totalCards = state.totalCards, onAdd = onNavigateToAdd)
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            StudyProgress(
                                current = viewModel.currentCardIndex + 1,
                                total = state.dueCards.size
                            )
                            StudyContent(
                                card = state.dueCards[viewModel.currentCardIndex],
                                isFlipped = viewModel.isFlipped,
                                onFlip = viewModel::flipCard,
                                onResult = viewModel::onReviewResult
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyProgress(current: Int, total: Int) {
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
private fun StudyContent(
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
private fun RatingButtons(onResult: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        Button(
            onClick = { onResult(1) },
            modifier = Modifier.weight(1f).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Otra vez", style = PlTypography.labelMedium, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = { onResult(3) },
            modifier = Modifier.weight(1f).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Container),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Difícil",
                style = PlTypography.labelMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = { onResult(4) },
            modifier = Modifier.weight(1f).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Bien", style = PlTypography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CardSide(text: String, label: String, modifier: Modifier = Modifier) {
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

@Composable
private fun StudyFinishedState(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PlSpacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "¡Sesión terminada!",
            style = PlTypography.headlineMedium,
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(PlSpacing.sm))
        Text(
            "Has repasado todas las tarjetas de hoy.",
            style = PlTypography.bodyLarge,
            color = PlColors.TextHint
        )
        Spacer(Modifier.height(PlSpacing.xl))
        PlButton(text = "Volver a cursos", onClick = onBack)
    }
}

@Composable
private fun NoDueCardsState(totalCards: Int, onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PlSpacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (totalCards == 0) {
            Text(
                "No hay tarjetas en este mazo",
                style = PlTypography.bodyLarge,
                color = PlColors.TextHint
            )
            Spacer(Modifier.height(PlSpacing.md))
            PlButton(text = "Crear primera tarjeta", onClick = onAdd)
        } else {
            Text(
                "¡Al día!",
                style = PlTypography.headlineMedium,
                color = PlColors.Primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(PlSpacing.sm))
            Text(
                "No hay tarjetas para repasar hoy.",
                style = PlTypography.bodyLarge,
                color = PlColors.TextHint
            )
            Text(
                "Vuelve mañana o añade nuevas tarjetas.",
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint
            )
        }
    }
}
