package com.unmsm.redfi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RedFiColorScheme = lightColorScheme(
    primary = RedFiGuinda,
    secondary = RedFiGold,
    background = RedFiLightBackground,
    surface = RedFiCardBackground,
)

@Composable
fun RedFiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RedFiColorScheme,
        content = content
    )
}