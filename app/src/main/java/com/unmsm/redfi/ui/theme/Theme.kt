package com.unmsm.redfi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RedFiGuinda,
    secondary = RedFiGold,
    background = RedFiLightBackground,
    surface = RedFiCardBackground,
)

private val DarkColorScheme = darkColorScheme(
    primary = RedFiGuinda,
    secondary = RedFiGold,
    background = Color(0xFF0B131E), // Fondo oscuro institucional profundo
    surface = Color(0xFF1C2533),     // Tarjetas y superficies oscuras
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

@Composable
fun RedFiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de tener definido tu Typography o elimínalo si no lo usas
        content = content
    )
}