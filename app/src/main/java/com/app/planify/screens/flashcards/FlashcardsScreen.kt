package com.app.planify.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlErrorMessage
import com.app.planify.components.PlLoader
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    courseId: String,
    viewModel: FlashcardsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToGenerate: () -> Unit = {}
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
                IconButton(onClick = onNavigateToGenerate) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = "Generar con IA",
                        tint = PlColors.Primary
                    )
                }
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
                                onResult = viewModel::onReviewResult,
                                onExplainClick = viewModel::explainCurrentCard
                            )
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showExplanation) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissExplanation,
            sheetState = sheetState,
            containerColor = PlColors.Surface
        ) {
            ExplanationSheetContent(
                isLoading = viewModel.isExplaining,
                text = viewModel.explanationText,
                error = viewModel.explanationError
            )
        }
    }
}

@Composable
private fun ExplanationSheetContent(
    isLoading: Boolean,
    text: String?,
    error: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PlSpacing.lg)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
        ) {
            Icon(
                Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = PlColors.Primary
            )
            Text(
                "Explicación",
                style = PlTypography.titleMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PlColors.Primary)
                }
            }
            error != null -> {
                Text(error, style = PlTypography.bodyMedium, color = PlColors.Error)
            }
            text != null -> {
                Text(text, style = PlTypography.bodyMedium, color = PlColors.TextMain)
            }
        }

        Spacer(Modifier.height(PlSpacing.sm))
    }
}
