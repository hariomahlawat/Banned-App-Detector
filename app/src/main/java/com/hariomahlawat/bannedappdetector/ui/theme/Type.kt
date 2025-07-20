package com.hariomahlawat.bannedappdetector.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.unit.sp
import com.hariomahlawat.bannedappdetector.R

private val provider = Provider(
    authority = "com.google.android.gms.fonts",
    packageName = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val sora = FontFamily(Font(GoogleFont("Sora"), provider, FontWeight.SemiBold))
private val poppins = FontFamily(Font(GoogleFont("Poppins"), provider, FontWeight.Medium))
private val inter = FontFamily(Font(GoogleFont("Inter"), provider, FontWeight.Normal))

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = sora,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)
