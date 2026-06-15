package com.app.planify.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PlLightColorScheme = lightColorScheme(
    primary            = PlPrimaryLight,
    onPrimary          = PlOnPrimaryLight,
    primaryContainer   = PlPrimaryContainerLight,
    onPrimaryContainer = PlOnPrimaryContainerLight,
    background         = PlBackgroundLight,
    surface            = PlSurfaceLight,
    onBackground       = PlOnBackgroundLight,
    onSurface          = PlOnSurfaceLight,
    outline            = PlOutlineLight,
    error              = PlErrorLight,
    onError            = PlOnErrorLight
)

private val PlDarkColorScheme = darkColorScheme(
    primary            = PlPrimaryDark,
    onPrimary          = PlOnPrimaryDark,
    primaryContainer   = PlPrimaryContainerDark,
    onPrimaryContainer = PlOnPrimaryContainerDark,
    background         = PlBackgroundDark,
    surface            = PlSurfaceDark,
    onBackground       = PlOnBackgroundDark,
    onSurface          = PlOnSurfaceDark,
    outline            = PlOutlineDark,
    error              = PlErrorDark,
    onError            = PlOnErrorDark
)

private const val THEME_ANIM_MS = 300

@Composable
fun PlanifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val target = if (darkTheme) PlDarkColorScheme else PlLightColorScheme
    val colorScheme = animateColorScheme(target)

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}

@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    val spec = tween<androidx.compose.ui.graphics.Color>(THEME_ANIM_MS)
    return target.copy(
        primary            = animateColorAsState(target.primary, spec, label = "primary").value,
        onPrimary          = animateColorAsState(target.onPrimary, spec, label = "onPrimary").value,
        primaryContainer   = animateColorAsState(target.primaryContainer, spec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(target.onPrimaryContainer, spec, label = "onPrimaryContainer").value,
        background         = animateColorAsState(target.background, spec, label = "background").value,
        surface            = animateColorAsState(target.surface, spec, label = "surface").value,
        onBackground       = animateColorAsState(target.onBackground, spec, label = "onBackground").value,
        onSurface          = animateColorAsState(target.onSurface, spec, label = "onSurface").value,
        outline            = animateColorAsState(target.outline, spec, label = "outline").value,
        error              = animateColorAsState(target.error, spec, label = "error").value,
        onError            = animateColorAsState(target.onError, spec, label = "onError").value
    )
}
