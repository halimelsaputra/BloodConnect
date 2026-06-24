package com.bloodconnect.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ---- Design tokens (from DESIGN.md, adapted for mobile) ----
val Rausch = Color(0xFFFF385C)
val RauschActive = Color(0xFFE00B41)
val RauschSoft = Color(0xFFFFF0F3)
val Ink = Color(0xFF222222)
val Body = Color(0xFF3F3F3F)
val Muted = Color(0xFF6A6A6A)
val Hairline = Color(0xFFDDDDDD)
val SurfaceSoft = Color(0xFFF7F7F7)
val SurfaceStrong = Color(0xFFF2F2F2)
val Success = Color(0xFF1F9D55)

private val LightColors = lightColorScheme(
    primary = Rausch,
    onPrimary = Color.White,
    primaryContainer = RauschSoft,
    onPrimaryContainer = RauschActive,
    secondary = Ink,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = Muted,
    outline = Hairline,
    error = Color(0xFFC13515),
    onError = Color.White
)

@Composable
fun BloodConnectTheme(content: @Composable () -> Unit) {
    // Keep a clean, light, Airbnb-like look regardless of system dark mode.
    @Suppress("UNUSED_VARIABLE")
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
