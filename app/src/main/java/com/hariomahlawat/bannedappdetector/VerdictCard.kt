package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.ErrorRed
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
    issues: IssueSummary,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasIssues = issues.total > 0

    val icon = if (hasIssues) Icons.Rounded.Error else Icons.Rounded.CheckCircle
    val iconTint = if (hasIssues) ErrorRed else SuccessGreen
    val title = if (hasIssues) "Attention Needed" else "All Clear!"
    val subtitle = if (hasIssues) {
        buildString {
            append("${issues.total} issue")
            if (issues.total > 1) append('s')
            append(" found in ${summary.total} apps")
        }
    } else {
        "Scanned ${summary.total} app${if (summary.total != 1) "s" else ""} – no issues."
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .glassCard(scrim = Color.Black.copy(.45f), shadowColor = iconTint),
        tonalElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {

            /* headline */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(title, color = iconTint, style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            /* chips – show only if any issue exists */
            if (hasIssues) {
                Spacer(Modifier.height(10.dp))
                    IssueChip("\uD83C\uDDE8\uD83C\uDDF3", issues.chinese)
                    IssueChip("⬇", issues.sideloaded)
                    IssueChip("\uD83D\uDEE0", issues.modded)
                    IssueChip("\uD83D\uDD12", issues.highRisk)
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(onClick = onViewDetails, colors = ButtonDefaults
                .filledTonalButtonColors(containerColor = iconTint)) {
                Text("View Details")
            }
        }
    }
}

/* pill-shaped counter */
@Composable
private fun IssueChip(symbol: String, count: Int) {
    if (count == 0) return
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .25f)
    ) {
        Text("$symbol $count",
             modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
             style = MaterialTheme.typography.labelSmall,
             color = BrandGold)
    }
}
