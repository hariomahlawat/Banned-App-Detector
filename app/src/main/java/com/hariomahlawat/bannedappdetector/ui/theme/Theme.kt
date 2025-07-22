package com.hariomahlawat.bannedappdetector.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import com.hariomahlawat.bannedappdetector.ThemeSetting
import com.hariomahlawat.bannedappdetector.ui.theme.AppTypography
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
@Composable
fun BannedAppDetectorTheme(
    theme: ThemeSetting = ThemeSetting.SYSTEM,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkTheme = when (theme) {
        ThemeSetting.SYSTEM -> isSystemInDarkTheme()
        ThemeSetting.DARK   -> true
        ThemeSetting.LIGHT  -> false
    }
    val dynamic = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    val colorScheme = dynamic.copy(
        primary = BrandGold,
        onPrimary = if (darkTheme) BgGradientStart else Color.Black
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
