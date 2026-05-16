package com.app.planify.logic.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class FontScale(val label: String, val scale: Float) {
    SMALL("Pequeño", 0.85f),
    NORMAL("Normal", 1.0f),
    LARGE("Grande", 1.15f)
}

object AppSettings {
    // null = follow system
    var isDarkTheme by mutableStateOf<Boolean?>(null)
    var fontScale by mutableStateOf(FontScale.NORMAL)
}
