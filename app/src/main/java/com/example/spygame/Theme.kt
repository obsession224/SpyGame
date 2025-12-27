package com.example.spygame

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColors(
    primary = Color(0xFF1E88E5),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White
)

@Composable
fun SpyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = DarkColors) {
        content()
    }
}
