package com.app.planify.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.Flashcard
import com.app.planify.components.PlInput
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
                IconButton(onClick = viewModel::openManageSheet) {
                    Icon(
                        Icons.Outlined.Tune,
                        contentDescription = "Administrar tarjetas",
                        tint = PlColors.TextMain
                    )
                }
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

    if (viewModel.showManageSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissManageSheet,
            sheetState = sheetState,
            containerColor = PlColors.Surface
        ) {
            ManageCardsSheetContent(
                cards = viewModel.allCards,
                onEdit = viewModel::startEditCard,
                onDelete = viewModel::askDeleteCard
            )
        }
    }

    viewModel.editingCard?.let {
        EditCardDialog(
            front = viewModel.editFront,
            back = viewModel.editBack,
            onFrontChange = viewModel::onEditFrontChange,
            onBackChange = viewModel::onEditBackChange,
            onConfirm = viewModel::saveEditCard,
            onDismiss = viewModel::cancelEditCard
        )
    }

    viewModel.deletingCard?.let {
        AlertDialog(
            onDismissRequest = viewModel::cancelDeleteCard,
            title = { Text("Eliminar tarjeta", style = PlTypography.titleMedium) },
            text = { Text("¿Seguro que deseas eliminar esta tarjeta? Esta acción no se puede deshacer.", style = PlTypography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDeleteCard) {
                    Text("Eliminar", color = PlColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDeleteCard) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ManageCardsSheetContent(
    cards: List<Flashcard>,
    onEdit: (Flashcard) -> Unit,
    onDelete: (Flashcard) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PlSpacing.lg)
            .navigationBarsPadding()
    ) {
        Text(
            "Administrar tarjetas",
            style = PlTypography.titleMedium,
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = PlSpacing.md)
        )

        if (cards.isEmpty()) {
            Text(
                "Aún no tienes tarjetas en este curso.",
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint,
                modifier = Modifier.padding(bottom = PlSpacing.lg)
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(PlSpacing.sm)
            ) {
                items(cards) { card ->
                    ManageCardRow(card = card, onEdit = { onEdit(card) }, onDelete = { onDelete(card) })
                }
            }
            Spacer(Modifier.height(PlSpacing.md))
        }
    }
}

@Composable
private fun ManageCardRow(
    card: Flashcard,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                card.front,
                style = PlTypography.bodyLarge,
                color = PlColors.TextMain,
                maxLines = 1
            )
            Text(
                card.back,
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint,
                maxLines = 1
            )
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = PlColors.Primary)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = PlColors.Error)
        }
    }
}

@Composable
private fun EditCardDialog(
    front: String,
    back: String,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar tarjeta") },
        text = {
            Column {
                PlInput(value = front, onValueChange = onFrontChange, label = "Pregunta")
                Spacer(Modifier.height(PlSpacing.md))
                PlInput(value = back, onValueChange = onBackChange, label = "Respuesta")
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = front.isNotBlank() && back.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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
