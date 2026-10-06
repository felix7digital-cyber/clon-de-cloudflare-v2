package com.cfadmin.pro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val CfOrange = Color(0xFFF38020)
val CfOrangeHover = Color(0xFFE06F14)
val CfDarkBg = Color(0xFF121212)
val CfCardBg = Color(0xFF1D1D1D)
val CfBorderDark = Color(0xFF2A2A2A)
val CfLightBg = Color(0xFFF8F9FA)
val CfLightCard = Color(0xFFFFFFFF)
val CfLightBorder = Color(0xFFE2E8F0)
val CfEmerald = Color(0xFF10B981)
val CfRed = Color(0xFFEF4444)
val CfAmber = Color(0xFFF59E0B)
val CfCyan = Color(0xFF06B6D4)
val CfPurple = Color(0xFFA855F7)
val CfBlue = Color(0xFF3B82F6)
val CfYellow = Color(0xFFEAB308)

private val DarkColors = darkColorScheme(
    primary = CfOrange,
    onPrimary = Color.White,
    primaryContainer = CfOrange.copy(alpha = 0.15f),
    onPrimaryContainer = CfOrange,
    secondary = CfEmerald,
    background = CfDarkBg,
    onBackground = Color(0xFFE5E7EB),
    surface = CfCardBg,
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = CfCardBg,
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = CfBorderDark,
    error = CfRed,
    onError = Color.White,
)

private val LightColors = lightColorScheme(
    primary = CfOrange,
    onPrimary = Color.White,
    primaryContainer = CfOrange.copy(alpha = 0.12f),
    onPrimaryContainer = CfOrange,
    secondary = CfEmerald,
    background = CfLightBg,
    onBackground = Color(0xFF1F2937),
    surface = CfLightCard,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = CfLightCard,
    onSurfaceVariant = Color(0xFF6B7280),
    outline = CfLightBorder,
    error = CfRed,
    onError = Color.White,
)

val CfTypography = Typography(
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 14.sp),
    bodyMedium = TextStyle(fontSize = 13.sp),
    bodySmall = TextStyle(fontSize = 12.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun CfAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = CfTypography,
        content = content
    )
}
