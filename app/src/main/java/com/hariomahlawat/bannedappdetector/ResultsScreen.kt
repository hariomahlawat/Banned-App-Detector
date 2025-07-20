package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.components.StatusChip
import com.hariomahlawat.bannedappdetector.util.getDeviceInfo
import com.hariomahlawat.bannedappdetector.util.saveToCache
import com.hariomahlawat.bannedappdetector.util.shareImage
import androidx.core.view.drawToBitmap
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
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Scan Results") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(painterResource(android.R.drawable.ic_menu_close_clear_cancel), contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    val bitmap = view.drawToBitmap()
                    val uri = bitmap.saveToCache(context)
                    shareImage(context, uri)
                }) {
                    Icon(painterResource(android.R.drawable.ic_menu_share), contentDescription = "Share")
                }
            }
        )
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(padding)) {
            item {
                val total = state.summary?.totalMonitored ?: 0
                val found = state.results.size
                val scanTime = state.lastScanAt?.let { DateFormat.getDateTimeInstance().format(Date(it)) } ?: "--"
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Total apps scanned: $total", style = MaterialTheme.typography.bodyMedium)
                    Text("Found banned apps: $found", style = MaterialTheme.typography.bodyMedium)
                    Text("Scan time: $scanTime", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.size(8.dp))
                    Text("Device: ${deviceInfo.manufacturer} ${deviceInfo.model}", style = MaterialTheme.typography.bodySmall)
                    Text("Android ID: ${deviceInfo.androidId}", style = MaterialTheme.typography.bodySmall)
                    Text("OS: ${deviceInfo.osVersion}", style = MaterialTheme.typography.bodySmall)
                }
            }
            items(state.results) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(result.meta.displayName, style = MaterialTheme.typography.bodyLarge)
                        Text(result.meta.packageName, style = MaterialTheme.typography.bodyMedium)
                    }
                    StatusChip(status = result.status)
                }
            }
        }
    }
}
