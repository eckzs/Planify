package com.app.planify.screens.flashcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PlSpacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFD4A017))
                )
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = null,
                    tint = Color(0xFFD4A017),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Pregunta",
                    style = PlTypography.labelLarge,
                    color = Color(0xFFD4A017),
                    fontWeight = FontWeight.Bold
                )
            }
            PlInput(
                value = viewModel.front,
                onValueChange = viewModel::onFrontChange,
                label = "Concepto o pregunta"
            )

            Spacer(Modifier.height(PlSpacing.sm))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PlSpacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF4CAF50))
                )
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Respuesta",
                    style = PlTypography.labelLarge,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
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
