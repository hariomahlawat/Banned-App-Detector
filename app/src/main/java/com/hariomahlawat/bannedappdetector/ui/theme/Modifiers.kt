package com.hariomahlawat.bannedappdetector.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape

fun Modifier.glassCard(
    scrim: Color = Color.Transparent,
    shadowColor: Color = Color.Transparent
) = this
    .shadow(
        elevation = 6.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = shadowColor,
        spotColor = shadowColor
    )
    .clip(RoundedCornerShape(24.dp))
    .background(scrim)
    .background(Color.White.copy(alpha = 0.08f))
    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
