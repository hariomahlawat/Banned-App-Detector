package com.hariomahlawat.bannedappdetector
/* ───────── imports ───────── */
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
import androidx.compose.material.icons.filled.Block
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
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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

/*──────────────────────── PUBLIC ENTRY ────────────────────────*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onViewResults: () -> Unit,
    onViewBannedApps: () -> Unit,
    onAiScan: () -> Unit,
    onToggleTheme: () -> Unit,
    dark: Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
    updateViewModel: UpdateViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val updateAvailable by updateViewModel.updateAvailable.collectAsState()
    var showUpdate by remember { mutableStateOf(false) }
    LaunchedEffect(updateAvailable) { if (updateAvailable) showUpdate = true }

    setSystemBars(
        color = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Box(
        Modifier
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
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Banned App Detector", color = BrandGold) },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "toggle theme",
                                tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { pad ->
            HomeContent(
                state = state,
                dark = dark,
                modifier = Modifier.padding(pad),
                triggerSystemScan = viewModel::onScan,
                navToResults = onViewResults,
                navToMonitoredList = onViewBannedApps,
                navToAiResults = onAiScan,
                onIncludeToggle = viewModel::setIncludeUnwanted
            )
        }

        if (showUpdate) UpdateDialog { showUpdate = false }
    }
}

/*──────────────────────── MAIN BODY ────────────────────────*/
@Composable
private fun HomeContent(
    state: HomeUiState,
    dark: Boolean,
    modifier: Modifier = Modifier,
    triggerSystemScan: () -> Unit,
    navToResults: () -> Unit,
    navToMonitoredList: () -> Unit,
    navToAiResults: () -> Unit,
    onIncludeToggle: (Boolean) -> Unit
) {
    /* progress anims */
    var bannedAnimating by remember { mutableStateOf(false) }
    val bannedProg = remember { Animatable(0f) }
    var aiAnimating by remember { mutableStateOf(false) }
    val aiProg = remember { Animatable(0f) }

    LaunchedEffect(bannedAnimating) {
        if (bannedAnimating) {
            bannedProg.snapTo(0f); bannedProg.animateTo(1f, tween(4000))
            navToResults(); bannedAnimating = false
        } else bannedProg.snapTo(0f)
    }
    LaunchedEffect(aiAnimating) {
        if (aiAnimating) {
            aiProg.snapTo(0f); aiProg.animateTo(1f, tween(4000))
            navToAiResults(); aiAnimating = false
        } else aiProg.snapTo(0f)
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* logo + chips */
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.archer_logo2),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.width(16.dp))
            TrustChipsColumn(dark)
        }

        Spacer(Modifier.height(12.dp))

        ElevatedAssistChip(
            onClick = navToMonitoredList,
            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) },
            label = { Text("Browse Monitored Apps") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        /*────────────  BANNED / UNWANTED CARD  ────────────*/
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Block, null, tint = BrandGold, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text("Banned / Unwanted Scan", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Find and disable banned or unwanted apps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = state.includeUnwanted,
                        onCheckedChange = onIncludeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                            uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = .3f
                            )
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Include Unwanted Apps")
                }

                Spacer(Modifier.height(12.dp))
                ScanButton(
                    isAnimating = bannedAnimating,
                    progress = bannedProg.value,
                    onClick = {
                        triggerSystemScan()
                        bannedAnimating = true
                    }
                )
                if (bannedProg.value > 0f) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { bannedProg.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = SuccessGreen,
                        trackColor = SuccessGreen.copy(alpha = .3f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        /*────────────  AI CARD  ────────────*/
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.VerifiedUser,
                    null,
                    tint = BrandGold,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("AI‑Based Privacy Scan", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Deep‑scan installed apps for sensitive permissions and risky behaviour.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                ScanButton(
                    isAnimating = aiAnimating,
                    progress = aiProg.value,
                    onClick = { aiAnimating = true }
                )
                if (aiProg.value > 0f) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { aiProg.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = SuccessGreen,
                        trackColor = SuccessGreen.copy(alpha = .3f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        /* summary appears after banned/unwanted scan */
        state.summary?.let {
            HomeSummaryCard(it, state.lastScanAt, navToResults)
            Spacer(Modifier.height(20.dp))
        }

        AppInfoFooter(Modifier.padding(bottom = 12.dp))
    }
}

/*────────────────── HELPERS ──────────────────*/
@Composable
fun TrustChipsColumn(dark: Boolean) {
    val bg = if (dark) MaterialTheme.colorScheme.surface.copy(alpha = .24f)
    else MaterialTheme.colorScheme.surfaceVariant
    Column {
        listOf(
            "AI‑Powered" to Icons.Filled.Memory,
            "Play Protect OK" to Icons.Filled.VerifiedUser,
            "No permissions" to Icons.Filled.Lock,
            "Offline Scan" to Icons.Filled.Shield
        ).forEach { (label, icon) ->
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(bg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun HomeSummaryCard(
    summary: SummaryStats,
    lastScanAt: Long?,
    onDetails: () -> Unit
) {
    Surface(
        modifier = Modifier
            .glassCard(MaterialTheme.colorScheme.surface.copy(alpha = .45f))
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .45f)
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
                    "Banned found: ${summary.installedEnabled}",
                    style = MaterialTheme.typography.bodyLarge, color = BrandGold
                )
                Text(
                    "Disabled: ${summary.installedDisabled}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onDetails,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("View Details", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

/* ScanButton kept identical to previous version */
/* ------------------------------------------------------------------
   1️⃣  PUT THIS BACK INTO HomeScreen.kt (or any other .kt you import)
 ------------------------------------------------------------------ */
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
                    trackColor = SuccessGreen.copy(alpha = .30f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )
            }
            Text(
                if (isAnimating) "Scanning…" else "Scan Now",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
/* ------------------------------------------------------------------ */
