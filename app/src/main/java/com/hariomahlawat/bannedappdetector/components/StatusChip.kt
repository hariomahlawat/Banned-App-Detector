package com.hariomahlawat.bannedappdetector.components


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hariomahlawat.bannedappdetector.MonitoredStatus


@Composable
fun StatusChip(status: MonitoredStatus) {
    val (label, colour) = when (status) {
        MonitoredStatus.INSTALLED_ENABLED -> "Enabled" to MaterialTheme.colorScheme.primary
        MonitoredStatus.INSTALLED_DISABLED -> "Disabled" to MaterialTheme.colorScheme.tertiary
        MonitoredStatus.NOT_INSTALLED -> "Not Installed" to MaterialTheme.colorScheme.outline
    }
    Surface(
        color = colour.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
