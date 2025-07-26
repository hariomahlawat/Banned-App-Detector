package com.hariomahlawat.bannedappdetector.permission

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.IssueSummary
import com.hariomahlawat.bannedappdetector.VerdictCard
import com.hariomahlawat.bannedappdetector.components.AppInfoFooter
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.ErrorRed
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import com.hariomahlawat.bannedappdetector.util.setSystemBars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRiskScreen(
    onBack: () -> Unit,
    dark: Boolean,
    viewModel: PermissionScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.runScan() }

    setSystemBars(
        color = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark) listOf(BgGradientStart, BgGradientEnd) else listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "AI-Based Privacy Scan",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (state.isScanning) {
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val chineseApps = state.results.filter { it.chineseOrigin }
                val highRisk = state.results.filter { !it.chineseOrigin && it.highRiskPermissions.isNotEmpty() }
                val mediumRisk = state.results.filter { !it.chineseOrigin && it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isNotEmpty() }
                state.results.filter { !it.chineseOrigin && it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isEmpty() }

                val sideloaded = state.results.filter { it.sideloaded }
                val modded = state.results.filter { it.modApp }
                val background = state.results.filter { it.backgroundPermissions.isNotEmpty() }

                val permissionMap = mutableMapOf<String, MutableList<String>>()
                state.results.forEach { report ->
                    report.highRiskPermissions.forEach { perm ->
                        permissionMap.getOrPut(perm) { mutableListOf() } += report.app.appName
                    }
                }

                LazyColumn(Modifier
                    .fillMaxSize()
                    .padding(padding)) {
                    item { Spacer(Modifier.height(24.dp)) }
                    state.summary?.let { summary ->
                        val issueSummary = IssueSummary(
                            chinese = chineseApps.size,
                            sideloaded = sideloaded.size,
                            modded = modded.size,
                            highRisk = highRisk.size
                        )
                        item {
                            VerdictCard(
                                totalApps = summary.total,
                                issues = issueSummary
                            )
                            Spacer(Modifier.height(8.dp))
                            DeveloperOptionsCard(state.developerOptionsEnabled)
                            Spacer(Modifier.height(16.dp))
                        }
                        val ratings = state.results.mapNotNull { it.rating }
                        val avg = if (ratings.isNotEmpty()) ratings.average().toFloat() else 0f
                        val lowCount = state.results.count { (it.rating != null && it.rating < 3f) || it.negativeReviewRatio > 0.3f }
                        val offenders = state.results.sortedByDescending { it.negativeReviewRatio }.take(3)
                        item {
                            RatingSummaryCard(avg, lowCount, offenders)
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Chinese Origin Apps",
                            count = chineseApps.size,
                            explanation = "These apps appear to be published by developers in China.",
                            shadowColor = if (chineseApps.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            if (chineseApps.isEmpty()) {
                                Text(
                                    "\u2705  No Chinese origin apps found",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                chineseApps.forEach { RiskRow(it) }
                            }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Direct APK Installs",
                            count = sideloaded.size,
                            explanation = "Apps installed from outside official stores.",
                            shadowColor = if (sideloaded.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            if (sideloaded.isEmpty()) {
                                Text(
                                    "\u2705  No sideloaded apps detected",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                sideloaded.forEach { RiskRow(it) }
                            }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Possible Mod Apps",
                            count = modded.size,
                            explanation = "These packages may have been modified or patched.",
                            shadowColor = if (modded.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            modded.forEach { RiskRow(it) }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Apps Listening in Background",
                            count = background.size,
                            explanation = "Apps requesting background permissions like microphone or location.",
                            shadowColor = if (background.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            background.forEach { RiskRow(it) }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "High Risk Apps",
                            count = highRisk.size,
                            explanation = "Apps requesting many dangerous permissions.",
                            shadowColor = if (highRisk.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            highRisk.forEach { RiskRow(it) }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Medium Risk Apps",
                            count = mediumRisk.size,
                            explanation = "Apps requesting some sensitive permissions.",
                            shadowColor = if (mediumRisk.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            mediumRisk.forEach { RiskRow(it) }
                        }
                    }

                    item { Spacer(Modifier.height(48.dp)) }
                }
            }
            AppInfoFooter(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun RiskRow(report: AppRiskReport) {
    ListItem(
        headlineContent = { Text(report.app.appName, color = BrandGold) },
        supportingContent = {
            val chinese = if (report.chineseOrigin) " 🇨🇳" else ""
            Column {
                Text(report.app.packageName + chinese, style = MaterialTheme.typography.bodySmall)
                report.reviewSnippets.firstOrNull()?.let { snippet ->
                    Text(
                        "\"$snippet\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text("Score: ${report.riskScore}", style = MaterialTheme.typography.labelSmall)
                report.rating?.let { r ->
                    val warn = r < 3f || report.negativeReviewRatio > 0.3f
                    val col = if (warn) ErrorRed else BrandGold
                    Text(
                        String.format("%.1f ★", r),
                        color = col,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = DividerDefaults.Thickness,
        color = DividerDefaults.color
    )
}


@Composable
private fun RatingSummaryCard(avgRating: Float, lowCount: Int, offenders: List<AppRiskReport>) {
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = .45f))
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Average rating: ${"%.2f".format(avgRating)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Low rated apps: $lowCount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (offenders.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Top offenders:", style = MaterialTheme.typography.bodySmall)
                offenders.forEach { rep ->
                    Text(
                        "- ${rep.app.appName} (${(rep.negativeReviewRatio * 100).toInt()}% negative)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskCategoryTile(
    title: String,
    count: Int,
    explanation: String,
    shadowColor: Color,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .glassCard(
                scrim = Color.Black.copy(alpha = .40f),
                shadowColor = shadowColor
            ),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandGold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandGold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = BrandGold
                )
            }

            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun DeveloperOptionsCard(enabled: Boolean) {
    val shadow = if (enabled) ErrorRed else SuccessGreen
    val title = if (enabled) "Developer Options Enabled" else "Developer Options Disabled"
    val message = if (enabled) {
        "Having developer options active can expose your device to tampering or debugging. Disable them for better security."
    } else {
        "Developer options are turned off which keeps your device configuration safe from accidental changes."
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .glassCard(scrim = Color.Black.copy(alpha = .40f), shadowColor = shadow),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = BrandGold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
