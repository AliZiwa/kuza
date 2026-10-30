package com.kuza.app.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF7C2D12),
    onPrimary = Color.White,
    surfaceVariant = Color(0xFFEDE9E4),
    onSurfaceVariant = Color(0xFF1F1B18),
)

private val Dark = darkColorScheme(
    primary = Color(0xFFF2B48A),
    onPrimary = Color(0xFF3A1406),
    surfaceVariant = Color(0xFF2A2623),
    onSurfaceVariant = Color(0xFFEDE9E4),
)

@Composable
fun KuzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        content = content,
    )
}
