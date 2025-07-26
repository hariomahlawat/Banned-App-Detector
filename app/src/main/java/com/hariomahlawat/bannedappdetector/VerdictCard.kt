package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.ErrorRed
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard

/* mirror of the Issue counters you already build in HomeScreen */
data class IssueSummary(
    val chinese: Int,
    val sideloaded: Int,
    val modded: Int,
    val highRisk: Int
) {
    val total get() = chinese + sideloaded + modded + highRisk
}

@Composable
fun VerdictCard(
    totalApps: Int,          // <-- NEW: pass the scan total
    issues: IssueSummary,
    modifier: Modifier = Modifier
) {
    val hasIssues = issues.total > 0

    /* dynamic visuals */
    val icon = if (hasIssues) Icons.Rounded.Error else Icons.Rounded.CheckCircle
    val iconTint = if (hasIssues) ErrorRed else SuccessGreen
    val title = if (hasIssues) "Attention needed" else "All clear!"

    val scanInfo = "Total $totalApps app${if (totalApps != 1) "s" else ""} scanned."
    val issueInfo = if (hasIssues) {
        "${issues.total} issue${if (issues.total > 1) "s" else ""} found"
    } else {
        "No issues found."
    }

    /* card container */
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .glassCard(scrim = Color.Black.copy(alpha = .45f), shadowColor = iconTint),
        tonalElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {

            /* headline row */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon, contentDescription = null,
                    tint = iconTint, modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        title, color = iconTint,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        scanInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        issueInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* issue list – only when problems exist */
            if (hasIssues) {
                Spacer(Modifier.height(10.dp))
                IssueLine("Chinese Origin Apps", issues.chinese)
                IssueLine("Direct APK Installs", issues.sideloaded)
                IssueLine("Possible Mod Apps", issues.modded)
                IssueLine("High Risk Apps", issues.highRisk)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

/* pill‑shaped counter chip */
@Composable
private fun IssueChip(symbol: String, count: Int) {
    if (count == 0) return
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .25f)
    ) {
        Text(
            "$symbol $count",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = BrandGold
        )
    }
}

@Composable
private fun IssueLine(label: String, count: Int) {
    if (count == 0) return
    Text(
        String.format("%02d %s", count, label),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
