package com.hariomahlawat.bannedappdetector

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.components.StatusChip
import java.text.DateFormat
import java.util.Date
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoot() {
    val vm: HomeViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            vm.dismissMessage()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Banned App Detector") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            // OPTION A: text-first variant (supports expanded)
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SummaryCard(state)
            Spacer(Modifier.height(8.dp))
            ResultsList(state.results)
        }
    }
}

@Composable
private fun SummaryCard(state: HomeUiState) {
    Card(Modifier.padding(12.dp).fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Summary", style = MaterialTheme.typography.titleMedium)
            val s = state.summary
            if (s == null) {
                Text("No scans yet")
            } else {
                Text("Total monitored: ${s.totalMonitored}")
                Text("Installed enabled: ${s.installedEnabled}")
                Text("Installed disabled: ${s.installedDisabled}")
                Text("Not installed: ${s.notInstalled}")
            }
            state.lastScanAt?.let {
                val text = DateFormat.getDateTimeInstance()
                    .format(Date(it))
                Text("Last scan: $text")
            }
        }
    }
}

@Composable
private fun ResultsList(results: List<ScanResult>) {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize()) {
        items(results, key = { it.meta.packageName }) { r ->
            ListItem(
                headlineContent = {
                    Text(r.meta.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                supportingContent = {
                    Text(r.meta.packageName, style = MaterialTheme.typography.labelSmall)
                },
                trailingContent = { StatusChip(r.status) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            "package:${r.meta.packageName}".toUri()
                        )
                        context.startActivity(intent)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        }
    }
}
