package com.hariomahlawat.bannedappdetector.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hariomahlawat.bannedappdetector.ui.theme.AppTypography
import com.hariomahlawat.bannedappdetector.ui.theme.ArcGlow
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGoldAlt
import com.hariomahlawat.bannedappdetector.ui.theme.NeutralGrey
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.WarningYellow

private val DarkColorScheme = darkColorScheme(
    primary = BrandGold,
    secondary = BrandGoldAlt,
    tertiary = WarningYellow,
    background = BgGradientStart,
    surface = BgGradientEnd,
    onPrimary = BgGradientStart,
    onBackground = BrandGoldAlt
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGold,
    secondary = BrandGoldAlt,
    tertiary = WarningYellow,
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.Black,
    onBackground = Color.Black
)

@Composable
fun BannedAppDetectorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
