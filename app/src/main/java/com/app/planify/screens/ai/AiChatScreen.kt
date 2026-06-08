package com.app.planify.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.api.models.ChatMessage
import com.app.planify.api.models.Course
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = viewModel()
) {
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.messages.size, viewModel.uiState) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem((viewModel.messages.size - 1).coerceAtLeast(0))
        }
    }

    Scaffold(
        topBar = { AiTopBar() },
        containerColor = PlColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (viewModel.messages.isEmpty() && viewModel.uiState !is AiChatUiState.Loading) {
                EmptyChat(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = PlSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(PlSpacing.sm),
                    contentPadding = PaddingValues(vertical = PlSpacing.md)
                ) {
                    items(viewModel.messages) { message ->
                        ChatBubble(message = message)
                    }
                    if (viewModel.uiState is AiChatUiState.Loading) {
                        item { LoadingBubble() }
                    }
                }
            }

            if (viewModel.uiState is AiChatUiState.Error) {
                val errorMsg = (viewModel.uiState as AiChatUiState.Error).message
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PlColors.Error)
                        .padding(horizontal = PlSpacing.md, vertical = PlSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        errorMsg,
                        style = PlTypography.bodyMedium,
                        color = PlColors.OnError,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::dismissError) {
                        Text("OK", color = PlColors.OnError)
                    }
                }
            }

            // Opción A: chips de acción rápida
            QuickActionsRow(
                onGenerateFlashcardsClick = viewModel::openGenerateSheet
            )

            ChatInputRow(
                value = viewModel.inputText,
                onValueChange = viewModel::onInputChange,
                onSend = viewModel::onSend,
                enabled = viewModel.uiState !is AiChatUiState.Loading
            )
        }
    }

    // Opción A: bottom sheet de generación rápida
    if (viewModel.showGenerateSheet) {
        GenerateFromChatSheet(viewModel = viewModel)
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun AiTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PlSpacing.md, vertical = PlSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = PlColors.Primary
        )
        Text("Asistente AI", style = PlTypography.headlineMedium, color = PlColors.TextMain)
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyChat(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = PlColors.Container,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(PlSpacing.md))
        Text(
            "Pregúntame cualquier cosa\nsobre tus materias",
            style = PlTypography.bodyLarge,
            color = PlColors.TextHint,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(PlSpacing.sm))
        Text(
            "O usa el chip ✨ para generar flashcards directamente",
            style = PlTypography.labelMedium,
            color = PlColors.TextHint,
            textAlign = TextAlign.Center
        )
    }
}

// ── Chat bubbles ──────────────────────────────────────────────────────────────

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) PlColors.Primary else PlColors.Surface)
                .padding(horizontal = PlSpacing.md, vertical = PlSpacing.sm)
        ) {
            Text(
                text = message.text,
                style = PlTypography.bodyMedium,
                color = if (isUser) PlColors.OnPrimary else PlColors.TextMain
            )
        }
    }
}

@Composable
private fun LoadingBubble() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                .background(PlColors.Surface)
                .padding(horizontal = PlSpacing.md, vertical = PlSpacing.sm)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = PlColors.Primary,
                strokeWidth = 2.dp
            )
        }
    }
}

// ── Quick actions (Opción A) ──────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(onGenerateFlashcardsClick: () -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PlSpacing.md, vertical = PlSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        item {
            SuggestionChip(
                onClick = onGenerateFlashcardsClick,
                label = { Text("✨ Generar flashcards", style = PlTypography.labelMedium) },
                icon = null,
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = PlColors.Container
                )
            )
        }
    }
}

// ── Generate from chat sheet (Opción A) ──────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenerateFromChatSheet(viewModel: AiChatViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = viewModel::closeGenerateSheet,
        sheetState = sheetState,
        containerColor = PlColors.Surface
    ) {
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
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = PlColors.Primary)
                Text(
                    "Generar flashcards",
                    style = PlTypography.titleMedium,
                    color = PlColors.TextMain,
                    fontWeight = FontWeight.Bold
                )
            }

            // Selector de curso
            if (viewModel.courses.isNotEmpty()) {
                CourseDropdown(
                    courses = viewModel.courses,
                    selectedCourse = viewModel.sheetSelectedCourse,
                    onCourseSelected = viewModel::onSheetCourseChange
                )
            }

            // Tema
            OutlinedTextField(
                value = viewModel.sheetTopic,
                onValueChange = viewModel::onSheetTopicChange,
                label = { Text("Tema o texto", style = PlTypography.bodyMedium) },
                placeholder = { Text("Ej: Derivadas, Segunda Guerra Mundial...", style = PlTypography.bodyMedium, color = PlColors.TextHint) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PlColors.Primary,
                    unfocusedBorderColor = PlColors.Container
                ),
                textStyle = PlTypography.bodyMedium,
                maxLines = 3
            )

            // Cantidad
            Text("Cantidad", style = PlTypography.labelMedium, color = PlColors.TextHint)
            Row(horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)) {
                listOf(5, 10, 15).forEach { count ->
                    val selected = viewModel.sheetCount == count
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onSheetCountChange(count) },
                        label = { Text("$count", style = PlTypography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PlColors.Primary,
                            selectedLabelColor = PlColors.OnPrimary
                        )
                    )
                }
            }

            Button(
                onClick = viewModel::onGenerateFromSheet,
                enabled = !viewModel.isGeneratingFromSheet && viewModel.sheetTopic.isNotBlank() && viewModel.sheetSelectedCourse != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PlColors.Primary)
            ) {
                Text(
                    if (viewModel.isGeneratingFromSheet) "Generando..." else "Generar y guardar",
                    style = PlTypography.labelMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDropdown(
    courses: List<Course>,
    selectedCourse: Course?,
    onCourseSelected: (Course) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCourse?.name ?: "Selecciona un curso",
            onValueChange = {},
            readOnly = true,
            label = { Text("Curso", style = PlTypography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PlColors.Primary,
                unfocusedBorderColor = PlColors.Container
            ),
            textStyle = PlTypography.bodyMedium
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.name, style = PlTypography.bodyMedium) },
                    onClick = {
                        onCourseSelected(course)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ── Input row ─────────────────────────────────────────────────────────────────

@Composable
private fun ChatInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    val canSend = enabled && value.isNotBlank()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PlColors.Surface)
            .padding(horizontal = PlSpacing.md, vertical = PlSpacing.sm),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Escribe tu pregunta...", style = PlTypography.bodyMedium, color = PlColors.TextHint) },
            modifier = Modifier.weight(1f),
            maxLines = 4,
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PlColors.Primary,
                unfocusedBorderColor = PlColors.Container
            ),
            textStyle = PlTypography.bodyMedium
        )
        IconButton(
            onClick = { if (canSend) onSend() },
            enabled = canSend,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(if (canSend) PlColors.Primary else PlColors.Container)
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Enviar",
                tint = if (canSend) PlColors.OnPrimary else PlColors.TextHint
            )
        }
    }
}
