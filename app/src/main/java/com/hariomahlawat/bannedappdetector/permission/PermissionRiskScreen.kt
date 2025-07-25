package com.hariomahlawat.bannedappdetector.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.WarningYellow
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import com.hariomahlawat.bannedappdetector.components.AppInfoFooter
import com.hariomahlawat.bannedappdetector.util.setSystemBars

private val ErrorRed = Color(0xFFE53935)
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
                    title = { Text("AI Scan Results", style = MaterialTheme.typography.headlineSmall) },
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
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val chineseApps = state.results.filter { it.chineseOrigin }
                val highRisk = state.results.filter { !it.chineseOrigin && it.highRiskPermissions.isNotEmpty() }
                val mediumRisk = state.results.filter { !it.chineseOrigin && it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isNotEmpty() }
                val lowRisk = state.results.filter { !it.chineseOrigin && it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isEmpty() }

                val sideloaded = state.results.filter { it.sideloaded }
                val modded = state.results.filter { it.modApp }
                val background = state.results.filter { it.backgroundPermissions.isNotEmpty() }

                val permissionMap = mutableMapOf<String, MutableList<String>>()
                state.results.forEach { report ->
                    report.highRiskPermissions.forEach { perm ->
                        permissionMap.getOrPut(perm) { mutableListOf() } += report.app.appName
                    }
                }

                LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                    item { Spacer(Modifier.height(24.dp)) }
                    state.summary?.let { summary ->
                        item {
                            SummaryCard(summary)
                            Spacer(Modifier.height(8.dp))
                            if (state.developerOptionsEnabled) {
                                Text(
                                    "\u26A0 Developer options enabled - please disable",
                                    color = WarningYellow,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            } else {
                                Text(
                                    "\u2705 Developer options are disabled",
                                    color = SuccessGreen,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    if (chineseApps.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "CHINESE ORIGIN APPS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(chineseApps) { report ->
                            RiskRow(report)
                        }
                    } else {
                        item {
                            Text(
                                "\u2705  No Chinese origin apps found",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    if (sideloaded.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "DIRECT APK INSTALLS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(sideloaded) { RiskRow(it) }
                    } else {
                        item {
                            Text(
                                "\u2705  No sideloaded apps detected",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    if (modded.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "POSSIBLE MOD APPS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(modded) { RiskRow(it) }
                    }

                    if (background.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "APPS LISTENING IN BACKGROUND",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(background) { RiskRow(it) }
                    }

                    if (permissionMap.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "HIGH RISK PERMISSIONS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        permissionMap.forEach { (perm, apps) ->
                            item {
                                Text(
                                    text = perm,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                            items(apps) { name ->
                                Text(
                                    text = "- $name",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    }

                    if (highRisk.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "HIGH RISK",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(highRisk) { RiskRow(it) }
                    }

                    if (mediumRisk.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "MEDIUM RISK",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(mediumRisk) { RiskRow(it) }
                    }

                    if (lowRisk.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = "LOW RISK",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(lowRisk) { RiskRow(it) }
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

@Composable
private fun RiskRow(report: AppRiskReport) {
    ListItem(
        headlineContent = { Text(report.app.appName, color = BrandGold) },
        supportingContent = {
            val chinese = if (report.chineseOrigin) " 🇨🇳" else ""
            Text(report.app.packageName + chinese, style = MaterialTheme.typography.bodySmall)
        },
        trailingContent = {
            Text("Score: ${report.riskScore}", style = MaterialTheme.typography.labelSmall)
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun SummaryCard(summary: PermissionScanSummary) {
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = .45f))
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricTile(summary.highRisk.toString(), "HIGH", Icons.Default.Warning, ErrorRed)
                MetricTile(summary.mediumRisk.toString(), "MED", Icons.Default.Warning, WarningYellow)
                MetricTile(summary.lowRisk.toString(), "LOW", Icons.Default.Inbox, SuccessGreen)
                MetricTile(summary.total.toString(), "SCANNED", Icons.Default.Inbox, BrandGold)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Chinese origin apps: ${summary.chinese}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricTile(value: String, label: String, icon: ImageVector, tint: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 0.dp, max = 80.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = tint)
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = tint)
        Spacer(Modifier.height(2.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
