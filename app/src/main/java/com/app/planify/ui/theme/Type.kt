package com.app.planify.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AppFont = FontFamily.Serif

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Bold,
        fontSize      = 30.sp,
        lineHeight    = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Bold,
        fontSize      = 24.sp,
        lineHeight    = 34.sp,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 20.sp,
        lineHeight    = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Medium,
        fontSize      = 17.sp,
        lineHeight    = 26.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 26.sp,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Medium,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.3.sp
    ),
    labelMedium = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Medium,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = AppFont,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.4.sp
    )
)
