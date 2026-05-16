package com.app.planify.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
