package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.drawToBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.components.AppInfoFooter
import com.hariomahlawat.bannedappdetector.components.StatusChip
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import com.hariomahlawat.bannedappdetector.util.DeviceInfo
import com.hariomahlawat.bannedappdetector.util.getDeviceInfo
import com.hariomahlawat.bannedappdetector.util.saveToCache
import com.hariomahlawat.bannedappdetector.util.shareImage
import com.hariomahlawat.bannedappdetector.util.setSystemBars
import java.text.DateFormat
import java.util.*

private val ErrorRed = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onBack: () -> Unit,
    dark: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val device = getDeviceInfo(context)

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
                TopAppBar(
                    title = { Text("Scan Results", style = MaterialTheme.typography.headlineSmall) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                view.drawToBitmap()
                                    .saveToCache(context)
                                    .let { shareImage(context, it) }
                            }
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_share),
                                contentDescription = "Share"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                ResultsBody(
                    state = state,
                    deviceInfo = device,
                    contentPadding = padding
                )
                AppInfoFooter(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun ResultsBody(
    state: HomeUiState,
    deviceInfo: DeviceInfo,
    contentPadding: PaddingValues
) {
    val bannedList = state.results.filter { it.meta.category == AppCategory.BANNED }
    val unwantedList = state.results.filter { it.meta.category == AppCategory.UNWANTED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        item {
            Spacer(Modifier.height(24.dp))

            val total = state.summary?.totalMonitored ?: 0
            val banned = bannedList.size
            val scanned = state.lastScanAt?.let {
                DateFormat.getDateTimeInstance().format(Date(it))
            } ?: "--"

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.archer_logo2),
                    contentDescription = null,
                    modifier = Modifier.size(96.dp)
                )

                Spacer(Modifier.height(16.dp))

                SummaryCard(
                    total = total,
                    banned = banned,
                    unwanted = unwantedList.size,
                    includeUnwanted = state.includeUnwanted,
                    scanTime = scanned,
                    deviceInfo = deviceInfo
                )
            }
        }

        if (bannedList.isNotEmpty()) {
            stickyHeader {
                Text(
                    text = "DETECTED BANNED APPS",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            items(bannedList) { result ->
                BannedAppRow(result)
            }
        }

        if (unwantedList.isNotEmpty()) {
            stickyHeader {
                Text(
                    text = "DETECTED UNWANTED APPS",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            items(unwantedList) { result ->
                BannedAppRow(result)
            }
        }

        item { Spacer(Modifier.height(48.dp)) }
    }
}

@Composable
private fun SummaryCard(
    total: Int,
    banned: Int,
    unwanted: Int,
    includeUnwanted: Boolean,
    scanTime: String,
    deviceInfo: DeviceInfo
) {
    Surface(
        modifier = Modifier
            .glassCard(Color.Black.copy(alpha = .45f))
            .fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            // metrics laid out as vertical tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricTile(
                    value = banned.toString(),
                    label = "BANNED",
                    icon = Icons.Default.Warning,
                    tint = ErrorRed,
                )
                MetricTile(
                    value = if (includeUnwanted) unwanted.toString() else "--",
                    label = "UNWANTED",
                    icon = Icons.Default.Warning,
                    tint = BrandGold,
                )
                MetricTile(
                    value = total.toString(),
                    label = "SCANNED",
                    icon = Icons.Default.Inbox,
                    tint = BrandGold,
                )
            }

            if (!includeUnwanted) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Unwanted apps were not scanned",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("Scan time: $scanTime", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            DeviceLine("Device", "${deviceInfo.manufacturer} ${deviceInfo.model}")
            DeviceLine("Android ID", deviceInfo.androidId)
            DeviceLine("OS", deviceInfo.osVersion)
        }
    }
}

@Composable
private fun MetricTile(
    value: String,
    label: String,
    icon: ImageVector,
    tint: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 0.dp, max = 80.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = tint
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = tint
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DeviceLine(label: String, data: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            data,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun BannedAppRow(result: ScanResult) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .glassCard(Color.Black.copy(alpha = .40f))
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = ErrorRed
                )
            },
            headlineContent = {
                Text(
                    result.meta.displayName,
                    color = BrandGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Text(result.meta.packageName, style = MaterialTheme.typography.bodySmall)
            },
            trailingContent = { StatusChip(result.status) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.heightIn(min = 72.dp)
        )
    }
}
