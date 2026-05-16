package com.app.planify.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.components.PlButton
import com.app.planify.components.PlCard
import com.app.planify.logic.utils.AppSettings
import com.app.planify.logic.utils.FontScale
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToAuth: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(PlSpacing.lg)
    ) {
        Text(
            text = "Perfil",
            style = PlTypography.headlineMedium,
            color = PlColors.TextMain,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(PlSpacing.lg))

        ProfileHeader(
            initials = viewModel.initials,
            displayName = viewModel.displayName,
            email = viewModel.email,
            major = viewModel.major,
            university = viewModel.university
        )

        Spacer(Modifier.height(PlSpacing.lg))

        SectionLabel("Apariencia")
        Spacer(Modifier.height(PlSpacing.sm))

        PlCard(modifier = Modifier.fillMaxWidth()) {
            ThemeRow(
                isDarkTheme = AppSettings.isDarkTheme,
                onToggle = { viewModel.cycleDarkTheme(context) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = PlSpacing.sm),
                color = PlColors.TextHint.copy(alpha = 0.15f)
            )
            FontSizeRow(
                current = AppSettings.fontScale,
                onSelect = { viewModel.setFontScale(context, it) }
            )
        }

        Spacer(Modifier.height(PlSpacing.lg))

        SectionLabel("Acerca de")
        Spacer(Modifier.height(PlSpacing.sm))

        PlCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = PlColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(PlSpacing.sm))
                    Text("Versión", style = PlTypography.bodyMedium, color = PlColors.TextMain)
                }
                Text("1.0", style = PlTypography.bodyMedium, color = PlColors.TextHint)
            }
        }

        Spacer(Modifier.height(PlSpacing.xl))

        PlButton(
            text = "Cerrar sesión",
            onClick = { viewModel.signOut(onNavigateToAuth) }
        )

        Spacer(Modifier.height(PlSpacing.xl))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = PlTypography.labelMedium,
        color = PlColors.TextHint,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ProfileHeader(
    initials: String,
    displayName: String,
    email: String,
    major: String?,
    university: String?
) {
    PlCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PlColors.Primary)
            ) {
                Text(
                    text = initials,
                    style = PlTypography.titleMedium,
                    color = PlColors.OnPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(PlSpacing.md))

            Column {
                Text(
                    text = displayName.ifBlank { "Estudiante" },
                    style = PlTypography.titleMedium,
                    color = PlColors.TextMain,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    style = PlTypography.bodyMedium,
                    color = PlColors.TextHint
                )
                if (!major.isNullOrBlank()) {
                    Spacer(Modifier.height(PlSpacing.xs))
                    Text(major, style = PlTypography.labelSmall, color = PlColors.TextHint)
                }
                if (!university.isNullOrBlank()) {
                    Text(university, style = PlTypography.labelSmall, color = PlColors.TextHint)
                }
            }
        }
    }
}

@Composable
private fun ThemeRow(isDarkTheme: Boolean?, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (isDarkTheme) {
                    true  -> Icons.Outlined.DarkMode
                    false -> Icons.Outlined.LightMode
                    null  -> Icons.Outlined.SettingsBrightness
                },
                contentDescription = null,
                tint = PlColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(PlSpacing.sm))
            Column {
                Text("Tema", style = PlTypography.bodyMedium, color = PlColors.TextMain)
                Text(
                    text = when (isDarkTheme) { true -> "Oscuro"; false -> "Claro"; else -> "Sistema" },
                    style = PlTypography.labelSmall,
                    color = PlColors.TextHint
                )
            }
        }
        Switch(
            checked = isDarkTheme == true,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = PlColors.OnPrimary,
                checkedTrackColor = PlColors.Primary,
                uncheckedThumbColor = PlColors.TextHint,
                uncheckedTrackColor = PlColors.Background
            )
        )
    }
}

@Composable
private fun FontSizeRow(current: FontScale, onSelect: (FontScale) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.FormatSize,
                contentDescription = null,
                tint = PlColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(PlSpacing.sm))
            Text("Tamaño de texto", style = PlTypography.bodyMedium, color = PlColors.TextMain)
        }
        Spacer(Modifier.height(PlSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
        ) {
            FontScale.entries.forEach { scale ->
                val selected = current == scale
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) PlColors.Primary else PlColors.Background)
                        .clickable { onSelect(scale) }
                        .padding(vertical = PlSpacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = scale.label,
                        style = PlTypography.labelMedium,
                        color = if (selected) PlColors.OnPrimary else PlColors.TextHint,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
