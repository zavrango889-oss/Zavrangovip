package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkNavyColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = PureBlack,
    primaryContainer = NeonRedMuted,
    onPrimaryContainer = TextPrimary,
    secondary = CyberBlueNeon,
    onSecondary = PureBlack,
    secondaryContainer = DarkNavyCardElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = CyberBlueBright,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorderHighlight,
    outlineVariant = DarkBorder,
    error = NeonRedCrimson,
    onError = TextPrimary
)

@Composable
fun ZavrangoOffzTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBackground.toArgb()
                window.navigationBarColor = DarkBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkNavyColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ZavrangoBoostTheme(
    content: @Composable () -> Unit
) = ZavrangoOffzTheme(content)
