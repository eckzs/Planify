package com.app.planify.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.app.planify.components.PlButton
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun StudyFinishedState(onBack: () -> Unit) {
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
fun NoDueCardsState(totalCards: Int, onAdd: () -> Unit) {
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
