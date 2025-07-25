package com.hariomahlawat.bannedappdetector

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.components.AppInfoFooter
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import com.hariomahlawat.bannedappdetector.util.setSystemBars
import java.text.DateFormat
import java.util.Date

@Composable
fun ScanButton(
    isAnimating: Boolean,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isAnimating,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            if (isAnimating) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = SuccessGreen,
                    trackColor = SuccessGreen.copy(alpha = 0.3f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
            Text(
                text = if (isAnimating) "Scanning…" else "Scan Now",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onViewResults: () -> Unit,
    onViewBannedApps: () -> Unit,
    onAiScan: () -> Unit,
    onToggleTheme: () -> Unit,
    dark: Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
    updateViewModel: UpdateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val updateAvailable by updateViewModel.updateAvailable.collectAsState()
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(updateAvailable) {
        if (updateAvailable) showUpdateDialog = true
    }

    setSystemBars(
        color = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (dark)
                        listOf(BgGradientStart, BgGradientEnd)
                    else
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Banned App Detector", color = BrandGold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            val icon = if (dark) Icons.Default.LightMode else Icons.Default.DarkMode
                            Icon(icon, contentDescription = "Toggle theme",
                                tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            HomeContent(
                state = state,
                onScanComplete = onViewResults,
                onViewResults = onViewResults,
                onViewBannedApps = onViewBannedApps,
                onAiScanComplete = onAiScan,
                onIncludeUnwantedChange = viewModel::setIncludeUnwanted,
                dark = dark,
                modifier = Modifier.padding(padding)
            )
        }

        if (showUpdateDialog) {
            UpdateDialog { showUpdateDialog = false }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onScanComplete: () -> Unit,
    onViewResults: () -> Unit,
    onViewBannedApps: () -> Unit,
    onAiScanComplete: () -> Unit,
    onIncludeUnwantedChange: (Boolean) -> Unit,
    dark: Boolean,
    modifier: Modifier = Modifier
) {
    var scanAnimating by remember { mutableStateOf(false) }
    val scanProgress = remember { Animatable(0f) }
    LaunchedEffect(scanAnimating) {
        if (scanAnimating) {
            scanProgress.snapTo(0f)
            scanProgress.animateTo(1f, tween(4000))
            onScanComplete()
            scanAnimating = false
        } else {
            scanProgress.snapTo(0f)
        }
    }

    var aiScanning by remember { mutableStateOf(false) }
    val aiProgress = remember { Animatable(0f) }
    LaunchedEffect(aiScanning) {
        if (aiScanning) {
            aiProgress.snapTo(0f)
            aiProgress.animateTo(1f, tween(4000))
            onAiScanComplete()
            aiScanning = false
        } else {
            aiProgress.snapTo(0f)
        }
    }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Two‑column header: logo + vertical chips
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.archer_logo2),
                    contentDescription = "App logo – archer",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.width(16.dp))
                TrustChipsColumn(dark)
            }

            Spacer(Modifier.height(12.dp))

            // Browse Monitored Apps button
            ElevatedAssistChip(
                onClick = onViewBannedApps,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                },
                label = { Text("Browse Monitored Apps") }
            )

            Spacer(Modifier.height(12.dp))
            // "Include Unwanted" toggle remains here
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.includeUnwanted,
                    onCheckedChange = onIncludeUnwantedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor   = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedTrackColor   = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Include Unwanted Apps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(12.dp))

            // Primary Scan
            ScanButton(
                isAnimating = scanAnimating,
                progress = scanProgress.value,
                onClick = { scanAnimating = true },
                modifier = Modifier.fillMaxWidth()
            )
            if (scanProgress.value > 0f) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { scanProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = SuccessGreen,
                    trackColor = SuccessGreen.copy(alpha = 0.3f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }

            Spacer(Modifier.height(16.dp))

            DividerWithText("Advanced Privacy Scan")
            Spacer(Modifier.height(12.dp))

            // AI Scan
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.VerifiedUser, null, tint = BrandGold, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "AI-Based Privacy Scan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Deeply analyze apps for sensitive permissions, risky behaviors, and app origin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { aiScanning = true },
                        enabled = !aiScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (aiScanning) "Scanning…" else "Start AI Scan",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    if (aiProgress.value > 0f) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { aiProgress.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = SuccessGreen,
                            trackColor = SuccessGreen.copy(alpha = 0.3f),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Summary & footer appear only after a scan
            state.summary?.let {
                SummaryCard(it, state.lastScanAt, onViewResults)
                Spacer(Modifier.height(16.dp))
                AppInfoFooter(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** Vertical chip column next to logo **/
@Composable
fun TrustChipsColumn(dark: Boolean) {
    val chipBg = if (dark)
        MaterialTheme.colorScheme.surface.copy(alpha = 0.24f)
    else
        MaterialTheme.colorScheme.surfaceVariant

    Column {
        listOf(
            "AI‑Powered"       to Icons.Filled.Memory,
            "Play Protect OK" to Icons.Filled.VerifiedUser,
            "No permissions"   to Icons.Filled.Lock,
            "Offline Scan"       to Icons.Filled.Shield
        ).forEach { (label, icon) ->
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(chipBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(
            Modifier.weight(1f),
            DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text, Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface)
        HorizontalDivider(
            Modifier.weight(1f),
            DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SummaryCard(
    summary: SummaryStats,
    lastScanAt: Long?,
    onViewResults: () -> Unit
) {
    Surface(
        Modifier
            .glassCard(MaterialTheme.colorScheme.surface.copy(alpha = 0.45f))
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Last scan: ${lastScanAt?.let { DateFormat.getDateTimeInstance().format(Date(it)) } ?: "--"}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Banned apps found: ${summary.installedEnabled}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BrandGold
                )
                Text(
                    "Disabled: ${summary.installedDisabled}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onViewResults,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("View Details", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
