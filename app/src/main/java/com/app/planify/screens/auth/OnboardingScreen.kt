package com.app.planify.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlButton
import com.app.planify.components.PlInput
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .padding(horizontal = PlSpacing.lg)
    ) {
        Spacer(Modifier.height(PlSpacing.lg))

        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Volver", tint = PlColors.TextMain)
        }

        Spacer(Modifier.height(PlSpacing.lg))

        Text("Completa tu perfil", style = PlTypography.headlineMedium, color = PlColors.TextMain)
        Spacer(Modifier.height(PlSpacing.xs))
        Text("Solo la primera vez", style = PlTypography.bodyMedium, color = PlColors.TextHint)

        Spacer(Modifier.height(PlSpacing.xl))

        PlInput(
            value = viewModel.name,
            onValueChange = viewModel::onNameChange,
            label = "Nombre completo"
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlInput(
            value = viewModel.career,
            onValueChange = viewModel::onCareerChange,
            label = "Major (opcional)"
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlInput(
            value = viewModel.university,
            onValueChange = viewModel::onUniversityChange,
            label = "Universidad (opcional)"
        )

        Spacer(Modifier.height(PlSpacing.xl))

        viewModel.errorMessage?.let { message ->
            Text(message, style = PlTypography.bodyMedium, color = PlColors.Error)
            Spacer(Modifier.height(PlSpacing.md))
        }

        PlButton(
            text = "Empezar",
            enabled = viewModel.name.isNotBlank() && !viewModel.isLoading,
            onClick = { viewModel.complete(onNavigateToHome) }
        )
    }
}
