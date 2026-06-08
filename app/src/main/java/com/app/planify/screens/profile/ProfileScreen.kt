package com.app.planify.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
import com.app.planify.components.PlInput
import com.app.planify.logic.utils.AppSettings
import com.app.planify.logic.utils.FontScale
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@OptIn(ExperimentalMaterial3Api::class)
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
            university = viewModel.university,
            onEditClick = viewModel::startEdit
        )

        Spacer(Modifier.height(PlSpacing.lg))

        // ── Estadísticas ───────────────────────────────────────────────────

        SectionLabel("Estadísticas")
        Spacer(Modifier.height(PlSpacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
        ) {
            StatCard(
                value = "${viewModel.stats.courses}",
                label = "Cursos",
                icon = Icons.Outlined.Book,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${viewModel.stats.flashcards}",
                label = "Flashcards",
                icon = Icons.Outlined.Style,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${viewModel.stats.tasksCompleted}",
                label = "Tareas",
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(PlSpacing.lg))

        // ── Cuenta ─────────────────────────────────────────────────────────

        SectionLabel("Cuenta")
        Spacer(Modifier.height(PlSpacing.sm))

        PlCard(modifier = Modifier.fillMaxWidth()) {
            AccountRow(
                icon = Icons.Outlined.Email,
                label = "Correo",
                value = viewModel.email.ifBlank { "—" }
            )
            if (viewModel.authProvider.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = PlSpacing.sm),
                    color = PlColors.TextHint.copy(alpha = 0.15f)
                )
                AccountRow(
                    icon = Icons.Outlined.Lock,
                    label = "Acceso",
                    value = viewModel.authProvider
                )
            }
            if (viewModel.createdAt.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = PlSpacing.sm),
                    color = PlColors.TextHint.copy(alpha = 0.15f)
                )
                AccountRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Miembro desde",
                    value = viewModel.createdAt
                )
            }
        }

        Spacer(Modifier.height(PlSpacing.lg))

        // ── Apariencia ─────────────────────────────────────────────────────

        SectionLabel("Apariencia")
        Spacer(Modifier.height(PlSpacing.sm))

        PlCard(modifier = Modifier.fillMaxWidth()) {
            ThemeRow(
                isDarkTheme = AppSettings.isDarkTheme,
                isResolvedDark = AppSettings.isDarkTheme ?: isSystemInDarkTheme(),
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

        // ── Acerca de ──────────────────────────────────────────────────────

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

    // ── Sheet de edición ───────────────────────────────────────────────────

    if (viewModel.isEditing) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::cancelEdit,
            sheetState = sheetState,
            containerColor = PlColors.Surface
        ) {
            EditProfileSheet(viewModel = viewModel)
        }
    }
}

// ── Componentes ───────────────────────────────────────────────────────────────

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
    university: String?,
    onEditClick: () -> Unit
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

            Column(modifier = Modifier.weight(1f)) {
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

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar perfil",
                    tint = PlColors.Primary
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    PlCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PlSpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PlColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = PlTypography.titleMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = PlTypography.labelSmall,
                color = PlColors.TextHint
            )
        }
    }
}

@Composable
private fun AccountRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PlColors.Primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(PlSpacing.sm))
            Text(label, style = PlTypography.bodyMedium, color = PlColors.TextMain)
        }
        Text(value, style = PlTypography.bodyMedium, color = PlColors.TextHint)
    }
}

@Composable
private fun EditProfileSheet(viewModel: ProfileViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PlSpacing.lg)
            .navigationBarsPadding()
            .padding(bottom = PlSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null, tint = PlColors.Primary)
            Text(
                "Editar perfil",
                style = PlTypography.titleMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
        }

        PlInput(
            value = viewModel.editName,
            onValueChange = viewModel::onEditNameChange,
            label = "Nombre completo"
        )
        PlInput(
            value = viewModel.editMajor,
            onValueChange = viewModel::onEditMajorChange,
            label = "Carrera (opcional)"
        )
        PlInput(
            value = viewModel.editUniversity,
            onValueChange = viewModel::onEditUniversityChange,
            label = "Universidad (opcional)"
        )

        viewModel.saveError?.let { err ->
            Text(err, style = PlTypography.bodyMedium, color = PlColors.Error)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
        ) {
            OutlinedButton(
                onClick = viewModel::cancelEdit,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar", color = PlColors.Primary)
            }
            Button(
                onClick = viewModel::saveEdit,
                enabled = !viewModel.isSaving,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PlColors.Primary)
            ) {
                Text(if (viewModel.isSaving) "Guardando..." else "Guardar")
            }
        }
    }
}

@Composable
private fun ThemeRow(isDarkTheme: Boolean?, isResolvedDark: Boolean, onToggle: () -> Unit) {
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
            checked = isResolvedDark,
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
