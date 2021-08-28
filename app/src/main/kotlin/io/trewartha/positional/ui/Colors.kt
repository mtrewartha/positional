package io.trewartha.positional.ui

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

private val Black = Color(0xFF000000)
private val Blue100 = Color(0xFFBBDEFB)
private val Blue300 = Color(0xFF64B5F6)
private val Blue300Dark = Color(0xFF2286C3)
private val Blue600 = Color(0xFF1976D2)
private val Blue700 = Color(0xFF1976D2)
private val Blue900 = Color(0xFF0D47A1)
private val Blue900Light = Color(0xFF5472D3)
private val Gray1 = Color(0xFFF9F9F9)
private val Gray2 = Color(0xFFF0F0F0)
private val Gray3 = Color(0xFF282828)
private val Gray4 = Color(0xFF121212)
private val DeepPurple100 = Color(0xFFD1C4E9)
private val DeepPurple300 = Color(0xFF9575CD)
private val DeepPurple300Dark = Color(0xFF65499C)
private val DeepPurple700 = Color(0xFF512DA8)
private val DeepPurple900 = Color(0xFF311B92)
private val DeepPurple900Light = Color(0xFF6746C3)
private val Red = Color(0xFFF44336)
private val White = Color(0xFFFFFFFF)

val DarkColors = darkColors(
    primary = Blue300,
    primaryVariant = Blue300Dark,
    secondary = DeepPurple300,
    secondaryVariant = DeepPurple300Dark,
    surface = Gray4,
    onSurface = White,
    error = Red
)

val LightColors = lightColors(
    primary = Blue900,
    primaryVariant = Blue900Light,
    secondary = DeepPurple900,
    secondaryVariant = DeepPurple900Light,
    surface = White,
    onSurface = Black,
    error = Red
)