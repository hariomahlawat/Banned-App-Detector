package com.hariomahlawat.bannedappdetector.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import com.hariomahlawat.bannedappdetector.util.BeepPlayer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun ScanProgressBar(
    bannedApps: List<String>,
    onScanFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val hits = remember {
        bannedApps.shuffled().map { it to Random.nextFloat() }.sortedBy { it.second }
    }
    val triggered = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(4000)) { value, _ ->
            hits.firstOrNull { it.second <= value }?.let { (app, _) ->
                if (app !in triggered) {
                    triggered += app
                    BeepPlayer.beep()
                }
            }
        }
        onScanFinished()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth(0.9f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (triggered.isEmpty()) Color(0xFF4CAF50) else Color.Gray)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.value)
                    .background(if (triggered.isEmpty()) Color(0xFF4CAF50) else Color.Red)
            )
            hits.forEach { (app, pos) ->
                if (app in triggered) {
                    DangerBubble(icon = Icons.Filled.Warning, offsetFraction = pos)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (triggered.isNotEmpty()) {
            triggered.forEach { Text("\u26A0\uFE0F  $it is banned", color = Color.Red) }
        } else if (progress.value == 1f) {
            Text("\u2705  No banned apps found", color = Color(0xFF4CAF50))
        }
    }
}

@Composable
private fun BoxScope.DangerBubble(icon: ImageVector, offsetFraction: Float) {
    Icon(
        icon,
        contentDescription = null,
        tint = Color.Red,
        modifier = Modifier
            .size(24.dp)
            .align(Alignment.CenterStart)
            .offset(x = this@DangerBubble.maxWidth * offsetFraction - 12.dp)
            .graphicsLayer {
                scaleX = 1.2f
                scaleY = 1.2f
            }
    )
}
