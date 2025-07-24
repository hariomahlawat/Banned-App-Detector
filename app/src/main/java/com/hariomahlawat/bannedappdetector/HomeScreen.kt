package com.hariomahlawat.bannedappdetector

import com.hariomahlawat.bannedappdetector.components.ScanProgressBar
import com.hariomahlawat.bannedappdetector.components.AppInfoFooter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.SuccessGreen
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import com.hariomahlawat.bannedappdetector.util.setSystemBars
import com.hariomahlawat.bannedappdetector.UpdateViewModel
import com.hariomahlawat.bannedappdetector.UpdateDialog
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onViewResults: () -> Unit,
    onViewBannedApps: () -> Unit,
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
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark) listOf(BgGradientStart, BgGradientEnd)
                    else listOf(
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
                    title = { Text("Banned App Detector") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = BrandGold
                    ),
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            val icon = if (dark) Icons.Default.LightMode else Icons.Default.DarkMode
                            Icon(icon, contentDescription = "Toggle theme")
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            HomeContent(
                state = state,
                onScan = viewModel::onScan,
                onScanFinished = viewModel::onScanAnimationFinished,
                onViewResults = onViewResults,
                onViewBannedApps = onViewBannedApps,
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
    onScan: () -> Unit,
    onScanFinished: () -> Unit,
    onViewResults: () -> Unit,
    onViewBannedApps: () -> Unit,
    onIncludeUnwantedChange: (Boolean) -> Unit,
    dark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.archer_logo2),
                contentDescription = "App logo – archer",
                modifier = Modifier.size(160.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Precision Scan",
                style = MaterialTheme.typography.displaySmall,
                color = BrandGold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "for Banned Apps",
                style = MaterialTheme.typography.headlineMedium,
                color = BrandGold,
                textAlign = TextAlign.Center
            )

            Text(
                "Local analysis. Targeted detection.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            ElevatedAssistChip(
                onClick = onViewBannedApps,
                label = { Text("Browse Monitored Apps") },
                leadingIcon = { Icon(Icons.Default.ArrowForward, contentDescription = null) }
            )

            Spacer(Modifier.height(24.dp))

            TrustChipsRow(dark)

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.includeUnwanted,
                    onCheckedChange = onIncludeUnwantedChange
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Include Unwanted Apps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onScan,
                enabled = !state.isScanning,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (state.isScanning) "Scanning…" else "Scan Now")
            }

            if (state.isScanning) {
                Spacer(Modifier.height(24.dp))
                ScanProgressBar(
                    bannedApps = state.results.map { it.meta.displayName },
                    onScanFinished = onScanFinished
                )
                Spacer(Modifier.height(24.dp))
            } else {
                Spacer(Modifier.height(24.dp))
            }

            state.summary?.let {
                SummaryCard(it, state.lastScanAt, onViewResults)
                Spacer(Modifier.height(24.dp))
            }
        }

        AppInfoFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun TrustChipsRow(dark: Boolean) {
    val chipBg = if (dark)
        Color.White.copy(alpha = 0.24f)
    else
        MaterialTheme.colorScheme.surfaceVariant

    val chipModifier = Modifier
        .padding(horizontal = 4.dp)
        .clip(RoundedCornerShape(50))
        .background(chipBg)
        .padding(horizontal = 12.dp, vertical = 4.dp)

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            "Offline scan" to Icons.Filled.Shield,
            "No permissions" to Icons.Filled.Lock,
            "Play Protect OK" to Icons.Filled.VerifiedUser
        ).forEach { (label, icon) ->
            Row(chipModifier, verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    summary: SummaryStats,
    lastScanAt: Long?,
    onViewResults: () -> Unit
) {
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = 0.45f))
            .fillMaxWidth()
            .semantics { heading() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Last scan: ${
                    lastScanAt?.let { DateFormat.getDateTimeInstance().format(Date(it)) } ?: "--"
                }",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

            Button(
                onClick = onViewResults,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View Details")
            }
        }
    }
}
