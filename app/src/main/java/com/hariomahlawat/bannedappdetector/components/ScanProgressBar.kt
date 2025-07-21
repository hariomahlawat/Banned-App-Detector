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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hariomahlawat.bannedappdetector.ScanResult
import com.hariomahlawat.bannedappdetector.util.SoundPoolSingleton
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import kotlin.random.Random

/**
 * Animated progress bar that reveals detected banned apps at random points along
 * the bar. Each reveal plays a short beep and adds the app name to a list
 * displayed below the bar.
 */
@Composable
fun ScanProgressBar(
    bannedApps: List<ScanResult>,
    onScanFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val hits = remember {
        bannedApps.shuffled().map { it to Random.nextFloat() }.sortedBy { it.second }
    }
    val triggered = remember { mutableStateListOf<ScanResult>() }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4000)
        ) { value, _ ->
            hits.firstOrNull { it.second <= value }?.let { (app, _) ->
                if (app !in triggered) {
                    triggered += app
                    SoundPoolSingleton.beep()
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
                .background(
                    if (triggered.isEmpty()) SuccessGreen else Color.Gray,
                    RoundedCornerShape(3.dp)
                )
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.value)
                    .background(
                        if (triggered.isEmpty()) SuccessGreen else Color.Red,
                        RoundedCornerShape(3.dp)
                    )
            )
            hits.forEach { (app, pos) ->
                if (app in triggered) {
                    DangerBubble(Icons.Filled.Warning, pos)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        if (triggered.isNotEmpty()) {
            triggered.forEach {
                Text(
                    text = "\u26A0\uFE0F  ${it.meta.displayName} is banned",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (progress.value == 1f) {
            Text(
                text = "\u2705  No banned apps found",
                color = SuccessGreen,
                style = MaterialTheme.typography.bodyMedium
            )
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
    )
}

