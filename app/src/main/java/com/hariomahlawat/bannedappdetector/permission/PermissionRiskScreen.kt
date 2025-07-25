package com.hariomahlawat.bannedappdetector.permission

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.ui.theme.*
import com.hariomahlawat.bannedappdetector.components.AppInfoFooter
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
        color     = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text("AI Scan Results", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (state.isScanning) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Prepare your data slices
                val high   = state.results.filter { it.highRiskPermissions.isNotEmpty() }
                val med    = state.results.filter { it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isNotEmpty() }
                val low    = state.results.filter { it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isEmpty() }
                val others = listOf(
                    OtherCategory("Chinese Origin Apps",     state.results.filter { it.chineseOrigin }, "From devs in China"),
                    OtherCategory("Direct APK Installs",     state.results.filter { it.sideloaded }, "Outside official store"),
                    OtherCategory("Possible Mod Apps",       state.results.filter { it.modApp },     "Modified or patched APKs"),
                    OtherCategory("Background‑Listening Apps", state.results.filter { it.backgroundPermissions.isNotEmpty() },
                        "Use mic/location in BG")
                )

                // ⬇️ Single outer LazyColumn ⬇️
                LazyColumn(
                    modifier        = Modifier.fillMaxSize(),
                    contentPadding  = PaddingValues(vertical = 8.dp)
                ) {
                    // 1) Summary card
                    item {
                        SummaryCard(state.summary!!)
                        Spacer(Modifier.height(8.dp))
                    }

                    // 2) Horizontal risk‑level tiles
                    item {
                        LazyRow(
                            contentPadding      = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                RiskTile("HIGH", high.size, Icons.Default.Warning, ErrorRed) { /* expand logic */ }
                            }
                            item {
                                RiskTile("MED", med.size,  Icons.Default.Warning, WarningYellow) { /* expand logic */ }
                            }
                            item {
                                RiskTile("LOW", low.size,  Icons.Default.Inbox,   SuccessGreen) { /* expand logic */ }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // 3) Inline expansion of whichever tile the user tapped
                    //    (you can keep track of `expandedCategory` in a viewModel or remember here)
                    //    e.g.:
                    // if (expandedCategory == "HIGH") items(high) { RiskRow(it) } etc.

                    // 4) The “other” categories
                    others.forEach { cat ->
                        item {
                            RiskCategoryTile(
                                title       = cat.title,
                                count       = cat.items.size,
                                explanation = cat.explanation
                            ) {
                                cat.items.forEach { RiskRow(it) }
                            }
                        }
                    }

                    // 5) Bottom padding for footer
                    item {
                        Spacer(Modifier.height(48.dp))
                    }
                }

                // 6) Always‑on footer
                AppInfoFooter(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}


data class OtherCategory(
    val title: String,
    val items: List<AppRiskReport>,
    val explanation: String
)

@Composable
private fun MixedResults(
    highRisk: List<AppRiskReport>,
    medRisk: List<AppRiskReport>,
    lowRisk: List<AppRiskReport>,
    otherCategories: List<OtherCategory>,
) {
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    // 2a) Risk‐level tiles
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            RiskTile("HIGH", highRisk.size, Icons.Default.Warning, ErrorRed) {
                expandedCategory = if (expandedCategory == "HIGH") null else "HIGH"
            }
        }
        item {
            RiskTile("MED", medRisk.size, Icons.Default.Warning, WarningYellow) {
                expandedCategory = if (expandedCategory == "MED") null else "MED"
            }
        }
        item {
            RiskTile("LOW", lowRisk.size, Icons.Default.Inbox, SuccessGreen) {
                expandedCategory = if (expandedCategory == "LOW") null else "LOW"
            }
        }
    }

    // Inline expansion of selected tile
    expandedCategory?.let { cat ->
        val list = when(cat) {
            "HIGH" -> highRisk
            "MED"  -> medRisk
            else   -> lowRisk
        }
        Column(Modifier.padding(horizontal = 16.dp)) {
            list.forEach { RiskRow(it) }
            Spacer(Modifier.height(12.dp))
        }
    }

    // 2b) Other categories as collapsible cards
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        otherCategories.forEach { cat ->
            item {
                RiskCategoryTile(
                    title = cat.title,
                    count = cat.items.size,
                    explanation = cat.explanation
                ) {
                    cat.items.forEach { RiskRow(it) }
                }
            }
        }
    }
}

@Composable
private fun RiskTile(
    label: String,
    count: Int,
    icon: ImageVector,
    tint: Color,
    onTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 80.dp)
            .clickable(onClick = onTap),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(count.toString(), style = MaterialTheme.typography.titleMedium, color = tint)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun RiskRow(report: AppRiskReport) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(report.app.appName, color = BrandGold, modifier = Modifier.weight(1f))
            Text("Score: ${report.riskScore}", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = BrandGold
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                if (report.highRiskPermissions.isNotEmpty()) {
                    Text(
                        "High‑risk perms: ${report.highRiskPermissions.joinToString()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (report.mediumRiskPermissions.isNotEmpty()) {
                    Text(
                        "Medium‑risk perms: ${report.mediumRiskPermissions.joinToString()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Score formula: high×3 + med×2 = " +
                            "${report.highRiskPermissions.size * 3 + report.mediumRiskPermissions.size * 2}",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                )
            }
        }
    }
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun RiskCategoryTile(
    title: String,
    count: Int,
    explanation: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.clickable { expanded = !expanded }.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = BrandGold, modifier = Modifier.weight(1f))
                Text(count.toString(), style = MaterialTheme.typography.titleMedium, color = BrandGold)
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = BrandGold
                )
            }
            AnimatedVisibility(expanded, enter = fadeIn(animationSpec = TweenSpec(300)), exit = fadeOut()) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(explanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: PermissionScanSummary) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MetricTile(summary.highRisk.toString(), "HIGH", Icons.Default.Warning, ErrorRed)
                MetricTile(summary.mediumRisk.toString(), "MED", Icons.Default.Warning, WarningYellow)
                MetricTile(summary.lowRisk.toString(), "LOW", Icons.Default.Inbox, SuccessGreen)
                MetricTile(summary.total.toString(), "SCANNED", Icons.Default.Inbox, BrandGold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Chinese origin apps: ${summary.chinese}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MetricTile(value: String, label: String, icon: ImageVector, tint: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 80.dp).padding(4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = tint)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = tint)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private val ErrorRed = Color(0xFFE53935)
