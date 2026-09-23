package com.kaimono.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ThemeOption(val key: String, val label: String, val description: String)

val themeOptions = listOf(
    ThemeOption("system", "System default", "Follows your device theme"),
    ThemeOption("light", "Kaimono Light", "Clean purple accent, easy daylight reading"),
    ThemeOption("dark", "Midnight", "Dark surfaces with soft violet accents"),
    ThemeOption("amoled", "AMOLED Black", "Pure black — battery friendly on OLED"),
    ThemeOption("paper", "Manga Paper", "Warm paper tones, like a printed volume"),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4), secondary = Color(0xFF625B71), tertiary = Color(0xFF7D5260))

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF), secondary = Color(0xFFCCC2DC), tertiary = Color(0xFFEFB8C8))

private val AmoledColors = darkColorScheme(
    primary = Color(0xFFB69CFF), secondary = Color(0xFFCCC2DC),
    background = Color(0xFF000000), surface = Color(0xFF0A0A0A), surfaceVariant = Color(0xFF141414))

private val PaperColors = lightColorScheme(
    primary = Color(0xFF8B5A2B), secondary = Color(0xFF6E5B43),
    background = Color(0xFFF7F2E7), surface = Color(0xFFFFFCF4), surfaceVariant = Color(0xFFEAE0CC))

@Composable
fun KaimonoTheme(themeKey: String, content: @Composable () -> Unit) {
    val dark = when (themeKey) {
        "light", "paper" -> false
        "dark", "amoled" -> true
        else -> isSystemInDarkTheme()
    }
    val colors = when (themeKey) {
        "light" -> LightColors
        "dark" -> DarkColors
        "amoled" -> AmoledColors
        "paper" -> PaperColors
        else -> if (dark) DarkColors else LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
