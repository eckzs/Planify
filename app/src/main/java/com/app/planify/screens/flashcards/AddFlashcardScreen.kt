package com.app.planify.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlButton
import com.app.planify.components.PlInput
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun AddFlashcardScreen(
    courseId: String,
    viewModel: AddFlashcardViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
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
                    "Nueva tarjeta",
                    style = PlTypography.headlineMedium,
                    color = PlColors.TextMain
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
            Text(
                "Pregunta",
                style = PlTypography.labelLarge,
                color = PlColors.TextHint
            )
            PlInput(
                value = viewModel.front,
                onValueChange = viewModel::onFrontChange,
                label = "Concepto o pregunta"
            )

            Spacer(Modifier.height(PlSpacing.xs))

            Text(
                "Respuesta",
                style = PlTypography.labelLarge,
                color = PlColors.TextHint
            )
            PlInput(
                value = viewModel.back,
                onValueChange = viewModel::onBackChange,
                label = "Definición o respuesta"
            )

            viewModel.errorMessage?.let { message ->
                Text(
                    text = message,
                    style = PlTypography.bodyMedium,
                    color = PlColors.Error
                )
            }

            Spacer(Modifier.weight(1f))

            PlButton(
                text = if (viewModel.isLoading) "Guardando..." else "Guardar tarjeta",
                onClick = { viewModel.createCard(courseId, onNavigateBack) },
                enabled = !viewModel.isLoading
            )
        }
    }
}
