package com.hariomahlawat.bannedappdetector.permission

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
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
import com.hariomahlawat.bannedappdetector.util.KeywordExtractor
import com.hariomahlawat.bannedappdetector.util.setSystemBars
import com.hariomahlawat.bannedappdetector.permission.AppRiskActionHandler
import kotlin.math.roundToInt

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
                            "AI-Based Security Scan",
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
            } else if (state.error != null) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            } else {
                val chineseApps = state.buckets.chinese
                val highRisk = state.buckets.high
                val mediumRisk = state.buckets.medium
                val sideloaded = state.buckets.sideloaded
                val modded = state.buckets.modded
                val background = state.buckets.background

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
                        val insights = state.reviewInsights
                        val lowThreshold = 3.5f
                        item {
                            insights?.let {
                                RatingsOverviewCard(
                                    avgRating = it.avgRating,
                                    totalApps = state.results.size,
                                    lowRated = it.lowRated,
                                    offenders = it.offenders,
                                    offline = it.offline
                                )
                                Spacer(Modifier.height(8.dp))
                                LowRatingAppsCard(it.lowRated, lowThreshold)
                                Spacer(Modifier.height(8.dp))
                                ReviewSentimentCard(
                                    reviewedCount = it.reviewedCount,
                                    offenders = it.offenders,
                                    negativeThreshold = 0.4f
                                )
                                Spacer(Modifier.height(16.dp))
                            }
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
                                chineseApps.forEach { RiskRow(it, viewModel) }
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
                                sideloaded.forEach { RiskRow(it, viewModel) }
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
                            modded.forEach { RiskRow(it, viewModel) }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Apps Listening in Background",
                            count = background.size,
                            explanation = "Apps requesting background permissions like microphone or location.",
                            shadowColor = if (background.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            background.forEach { RiskRow(it, viewModel) }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "High Risk Apps",
                            count = highRisk.size,
                            explanation = "Apps requesting many dangerous permissions.",
                            shadowColor = if (highRisk.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            highRisk.forEach { RiskRow(it, viewModel) }
                        }
                    }

                    item {
                        RiskCategoryTile(
                            title = "Medium Risk Apps",
                            count = mediumRisk.size,
                            explanation = "Apps requesting some sensitive permissions.",
                            shadowColor = if (mediumRisk.isEmpty()) SuccessGreen else ErrorRed,
                        ) {
                            mediumRisk.forEach { RiskRow(it, viewModel) }
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
private fun RiskRow(report: AppRiskReport, actions: AppRiskActionHandler) {
    ListItem(
        headlineContent = { Text(report.app.appName, color = BrandGold) },
        supportingContent = {
            val chinese = if (report.chineseOrigin) " 🇨🇳" else ""
            Column {
                Text(report.app.packageName + chinese, style = MaterialTheme.typography.bodySmall)
                report.reviews.firstOrNull()?.let { snippet ->
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
                Row {
                    IconButton(onClick = { actions.openSettings(report.app.packageName) }) {
                        Icon(painterResource(android.R.drawable.ic_menu_manage), contentDescription = "Open settings", tint = BrandGold)
                    }
                    IconButton(onClick = { actions.promptUninstall(report.app.packageName) }) {
                        Icon(painterResource(android.R.drawable.ic_menu_delete), contentDescription = "Uninstall", tint = BrandGold)
                    }
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


@SuppressLint("DefaultLocale")
@Composable
private fun RatingsOverviewCard(
    avgRating: Float,
    totalApps: Int,
    lowRated: List<AppRiskReport>,
    offenders: List<AppRiskReport>,
    offline: Boolean
) {
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = .45f))
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                String.format("%.1f / 5 across %d scanned apps", avgRating, totalApps),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (offline) {
                Spacer(Modifier.height(2.dp))
                Text(
                    "Some rating data may be outdated.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(Modifier.height(8.dp))
            val lowNames = lowRated.joinToString { it.app.appName }
            Text(
                "${lowRated.size} apps below threshold${if (lowNames.isNotBlank()) ": $lowNames" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${offenders.size} apps with high negative-review ratio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun LowRatingAppsCard(apps: List<AppRiskReport>, threshold: Float) {
    if (apps.isEmpty()) return
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = .45f))
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Low-Rating Apps", style = MaterialTheme.typography.titleMedium, color = BrandGold)
            Spacer(Modifier.height(8.dp))
            apps.forEach { rep ->
                Row(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)) {
                    AppIcon(rep.app.packageName)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(rep.app.appName, style = MaterialTheme.typography.bodyMedium)
                        if (rep.reviewCount < 10) {
                            Text("Not enough data", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(String.format("%.1f", rep.rating), style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.width(4.dp))
                                StarsRow(rep.rating ?: 0f, highlight = rep.rating != null && rep.rating < threshold)
                            }
                        }
                        rep.reviews.firstOrNull()?.let { snippet ->
                            Text(
                                "\"$snippet\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewSentimentCard(
    reviewedCount: Int,
    offenders: List<AppRiskReport>,
    negativeThreshold: Float
) {
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = .45f))
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Reviews scanned for $reviewedCount apps",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (offenders.isEmpty()) {
                Text(
                    "No apps with significant negative-review trends detected.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Spacer(Modifier.height(4.dp))
                offenders.forEach { rep ->
                    Column(Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)) {
                        val caution = MaterialTheme.colorScheme.error
                        val keywords = KeywordExtractor().topKeywords(rep.reviews)
                        Text(rep.app.appName, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${(rep.negativeReviewRatio * 100).toInt()}% negative",
                            color = if (rep.negativeReviewRatio > negativeThreshold) caution else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (keywords.isNotEmpty()) {
                            Text(keywords.joinToString(), style = MaterialTheme.typography.bodySmall)
                        }
                        rep.reviews.firstOrNull()?.let { snippet ->
                            Text(
                                "\"$snippet\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIcon(packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val drawable = remember(packageName) {
        try { context.packageManager.getApplicationIcon(packageName) } catch (_: Exception) { null }
    }
    if (drawable != null) {
        val bitmap = remember(drawable) { drawable.toBitmap().asImageBitmap() }
        Image(bitmap = bitmap, contentDescription = null, modifier = modifier.size(24.dp))
    } else {
        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = modifier.size(24.dp))
    }
}

@Composable
private fun StarsRow(rating: Float, highlight: Boolean) {
    Row {
        val color = if (highlight) MaterialTheme.colorScheme.error else BrandGold
        repeat(5) { i ->
            val icon = if (i < rating.roundToInt()) Icons.Filled.Star else Icons.Outlined.Star
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
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
