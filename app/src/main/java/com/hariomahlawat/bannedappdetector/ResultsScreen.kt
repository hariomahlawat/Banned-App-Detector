package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.components.StatusChip
import com.hariomahlawat.bannedappdetector.util.getDeviceInfo
import com.hariomahlawat.bannedappdetector.util.saveToCache
import com.hariomahlawat.bannedappdetector.util.shareImage
import androidx.core.view.drawToBitmap
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val deviceInfo = getDeviceInfo(context)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val bitmap = view.drawToBitmap()
                        val uri = bitmap.saveToCache(context)
                        shareImage(context, uri)
                    }) {
                        Icon(
                            painterResource(android.R.drawable.ic_menu_share),
                            contentDescription = "Share"
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd))
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    val total = state.summary?.totalMonitored ?: 0
                    val found = state.results.size
                    val scanTime = state.lastScanAt?.let {
                        DateFormat.getDateTimeInstance().format(Date(it))
                    } ?: "--"

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.archer_logo),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Surface(modifier = Modifier.glassCard().fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "Total Apps scanned: $total",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BrandGold
                                )
                                Text(
                                    "Banned Apps Found: $found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BrandGold
                                )
                                Text(
                                    "Scan time: $scanTime",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BrandGold
                                )
                                Spacer(Modifier.size(8.dp))
                                Text(
                                    "Device: ${'$'}{deviceInfo.manufacturer} ${'$'}{deviceInfo.model}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Android ID: ${'$'}{deviceInfo.androidId}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "OS: ${'$'}{deviceInfo.osVersion}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        "List of Banned Apps",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandGold
                    )
                }
                items(state.results) { result ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .glassCard()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    result.meta.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BrandGold
                                )
                                Text(
                                    result.meta.packageName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            StatusChip(status = result.status)
                        }
                    }
                }
            }
        }
    }
}
