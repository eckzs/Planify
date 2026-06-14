package com.app.planify.screens.flashcards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.GeneratedFlashcard
import com.app.planify.components.PlButton
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun GenerateFlashcardsScreen(
    courseId: String,
    viewModel: GenerateFlashcardsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(courseId) {
        viewModel.loadCourse(courseId)
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
                    "Generar con IA",
                    style = PlTypography.headlineMedium,
                    color = PlColors.TextMain,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = PlColors.Primary
                )
            }
        },
        containerColor = PlColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(PlSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PlSpacing.md)
        ) {
            if (viewModel.generatedCards.isEmpty()) {
                GenerateForm(viewModel = viewModel)
            } else {
                GeneratedCardsPreview(
                    cards = viewModel.generatedCards,
                    isSaving = viewModel.isSaving,
                    onSave = { viewModel.onSaveAll(courseId, onNavigateBack) },
                    onRegenerate = viewModel::onGenerate
                )
            }

            viewModel.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    style = PlTypography.bodyMedium,
                    color = PlColors.Error
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.GenerateForm(viewModel: GenerateFlashcardsViewModel) {
    Text(
        "Pega un texto, apuntes o escribe un tema",
        style = PlTypography.labelLarge,
        color = PlColors.TextHint
    )

    OutlinedTextField(
        value = viewModel.inputText,
        onValueChange = viewModel::onInputChange,
        placeholder = {
            Text(
                "Ej: La fotosíntesis es el proceso por el cual...",
                style = PlTypography.bodyMedium,
                color = PlColors.TextHint
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        maxLines = 8,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PlColors.Primary,
            unfocusedBorderColor = PlColors.Container
        ),
        textStyle = PlTypography.bodyMedium
    )

    Text("Cantidad de tarjetas", style = PlTypography.labelLarge, color = PlColors.TextMain)

    Row(horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)) {
        listOf(5, 10, 15).forEach { count ->
            val selected = viewModel.selectedCount == count
            FilterChip(
                selected = selected,
                onClick = { viewModel.onCountChange(count) },
                label = { Text("$count", style = PlTypography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PlColors.Primary,
                    selectedLabelColor = PlColors.OnPrimary
                )
            )
        }
    }

    Spacer(Modifier.weight(1f))

    PlButton(
        text = if (viewModel.isGenerating) "Generando..." else "Generar tarjetas",
        onClick = viewModel::onGenerate,
        enabled = !viewModel.isGenerating
    )
}

@Composable
private fun ColumnScope.GeneratedCardsPreview(
    cards: List<GeneratedFlashcard>,
    isSaving: Boolean,
    onSave: () -> Unit,
    onRegenerate: () -> Unit
) {
    Text(
        "${cards.size} tarjetas generadas",
        style = PlTypography.titleMedium,
        color = PlColors.TextMain,
        fontWeight = FontWeight.Bold
    )

    LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        items(cards) { card ->
            GeneratedCardItem(card = card)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        OutlinedButton(
            onClick = onRegenerate,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, PlColors.Primary)
        ) {
            Text("Regenerar", color = PlColors.Primary)
        }
        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Primary)
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar todas")
        }
    }
}

@Composable
private fun GeneratedCardItem(card: GeneratedFlashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PlColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(PlSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PlSpacing.sm)
        ) {
            Text(
                card.front,
                style = PlTypography.bodyMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = PlColors.Container)
            Text(card.back, style = PlTypography.bodyMedium, color = PlColors.TextHint)
        }
    }
}
