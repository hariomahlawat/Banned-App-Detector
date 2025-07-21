package com.hariomahlawat.bannedappdetector.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hariomahlawat.bannedappdetector.util.BeepPlayer
import kotlin.random.Random

@Composable
fun ScanProgressBar(
    bannedApps: List<String>,
    onScanFinished: () -> Unit
) {
    val progress   = remember { Animatable(0f) }

    // randomised hit positions
    val hits = remember {
        bannedApps.shuffled()
            .map { it to Random.nextFloat() }
            .sortedBy { it.second }
    }
    val triggered = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = 4_000)
        ) {         // this lambda has Animatable as **receiver**
            // `value` is current progress inside the receiver scope
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
        /* ---------- progress track ---------- */
        BoxWithConstraints(                                 // <— changed
            Modifier
                .fillMaxWidth(0.9f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (triggered.isEmpty()) Color(0xFF4CAF50) else Color.Gray)
        ) {
            /* moving bar */
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.value)
                    .background(
                        if (triggered.isEmpty()) Color(0xFF4CAF50) else Color.Red
                    )
            )

            /* danger bubbles */
            val barWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
            hits.forEach { (app, pos) ->
                if (app in triggered) {
                    DangerBubble(
                        icon       = Icons.Filled.Warning,
                        xOffsetDp  = with(LocalDensity.current) { (barWidthPx * pos).toDp() }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /* scan log */
        when {
            triggered.isNotEmpty() ->
                triggered.forEach {
                    Text(
                        text = "⚠️  $it is banned",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            progress.value == 1f ->
                Text(
                    text = "✅  No banned apps found",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.titleMedium
                )
        }
    }
}

@Composable
private fun BoxScope.DangerBubble(
    icon: ImageVector,
    xOffsetDp: Dp                           // explicit offset passed in
) {
    Icon(
        icon,
        contentDescription = null,
        tint = Color.Red,
        modifier = Modifier
            .size(24.dp)
            .offset(x = xOffsetDp - 12.dp) // centre icon on the hit
            .graphicsLayer {
                scaleX = 1.2f; scaleY = 1.2f    // little “pop”
            }
    )
}
