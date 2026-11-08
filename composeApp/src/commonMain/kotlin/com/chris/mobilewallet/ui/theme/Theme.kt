// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/theme/Theme.kt
package com.chris.mobilewallet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SafaricomGreen,
    onPrimary = Color.White,
    secondary = SafaricomDarkGreen,
    error = MpesaRed,
    background = BackgroundLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = SafaricomGreen,
    onPrimary = Color.White,
    secondary = SafaricomDarkGreen,
    error = ErrorRed,
    background = BackgroundDark,
    surface = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

@Composable
fun MobileWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
