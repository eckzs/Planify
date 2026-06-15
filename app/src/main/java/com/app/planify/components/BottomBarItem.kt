package com.app.planify.components

import androidx.compose.ui.graphics.vector.ImageVector

data class PlNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)
