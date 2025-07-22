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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

/* ---------- 1. Top bar with app name ---------- */
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
    LaunchedEffect(updateAvailable) { if (updateAvailable) showUpdateDialog = true }
    setSystemBars(
        color = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark)
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
                CenterAlignedTopAppBar(                    // Material3 app‑bar
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
            containerColor = Color.Transparent           // keep gradient visible
        ) { padding ->
            HomeContent(
                state            = state,
                onScan           = viewModel::onScan,
                onScanFinished   = viewModel::onScanAnimationFinished,
                onViewResults    = onViewResults,
                onViewBannedApps = onViewBannedApps,
                dark             = dark,
                modifier         = Modifier.padding(padding)   // scaffold inset
            )
        }
        if (showUpdateDialog) {
            UpdateDialog { showUpdateDialog = false }
        }
    }
}

/* ---------- 2. Body ------------ */
@Composable
private fun HomeContent(
    state: HomeUiState,
    onScan: () -> Unit,
    onScanFinished: () -> Unit,
    onViewResults: () -> Unit,
    onViewBannedApps: () -> Unit,
    dark: Boolean,
    modifier: Modifier = Modifier            // new param
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* logo */
            Image(
                painter  = painterResource(R.drawable.archer_logo2),
                contentDescription = "App logo – archer",
                modifier = Modifier.size(160.dp)
            )

            /* hero text */
            Text(
                text      = "Precision Scan",
                style     = MaterialTheme.typography.displaySmall,
                color     = BrandGold,
                textAlign = TextAlign.Center,
            )
            Text(
                text      = "for Banned Apps",
                style     = MaterialTheme.typography.headlineMedium,
                color     = BrandGold,
                textAlign = TextAlign.Center,
            )

            /* tag line */
            Text(
                "Local analysis. Targeted detection.",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            ElevatedAssistChip(
                onClick = onViewBannedApps,
                label = { Text("Browse Army Banned Apps") },
                leadingIcon = { Icon(Icons.Default.ArrowForward, contentDescription = null) }
            )
            Spacer(Modifier.height(32.dp))

            /* ---------- trust / privacy chips ---------- */
            TrustChipsRow(dark)
            Spacer(Modifier.height(28.dp))

            /* scan button */
            Button(
                onClick = onScan,
                enabled = !state.isScanning,                     // avoid double‑tap
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
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

            /* progress bar */
            if (state.isScanning) {
                Spacer(Modifier.height(20.dp))
                ScanProgressBar(
                    bannedApps     = state.results.map { it.meta.displayName },
                    onScanFinished = onScanFinished
                )
                Spacer(Modifier.height(32.dp))
            } else Spacer(Modifier.height(32.dp))

            /* results card */
            state.summary?.let { SummaryCard(it, state.lastScanAt, onViewResults) }

            Spacer(Modifier.height(40.dp))
        }

        AppInfoFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}

/* ---------- 3. Trust chips ---------- */
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
        verticalAlignment     = Alignment.CenterVertically
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

/* ---------- 4. Result summary ---------- */
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
            .semantics { heading() }          // accessibility
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
                onClick  = onViewResults,
                modifier = Modifier.align(Alignment.End)
            ) { Text("View Details") }
        }
    }
}

