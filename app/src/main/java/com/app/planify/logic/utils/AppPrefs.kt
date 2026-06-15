package com.app.planify.logic.utils

import android.content.Context

object AppPrefs {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_FONT_SCALE = "font_scale"

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        AppSettings.isDarkTheme = when (prefs.getString(KEY_DARK_THEME, "system")) {
            "dark"  -> true
            "light" -> false
            else    -> null
        }
        val saved = prefs.getString(KEY_FONT_SCALE, FontScale.NORMAL.name)
        AppSettings.fontScale = FontScale.entries.firstOrNull { it.name == saved } ?: FontScale.NORMAL
    }

    fun setDarkTheme(context: Context, value: Boolean?) {
        AppSettings.isDarkTheme = value
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_DARK_THEME, when (value) { true -> "dark"; false -> "light"; else -> "system" })
            .apply()
    }

    fun setFontScale(context: Context, scale: FontScale) {
        AppSettings.fontScale = scale
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_FONT_SCALE, scale.name)
            .apply()
    }
}
