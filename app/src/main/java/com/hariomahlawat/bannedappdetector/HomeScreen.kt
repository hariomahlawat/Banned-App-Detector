package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.ui.theme.glassCard
import java.text.DateFormat
import java.util.Date

@Composable
fun HomeScreen(
    onViewResults: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    HomeContent(state = state, onScan = viewModel::onScan, onViewResults = onViewResults)
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onScan: () -> Unit,
    onViewResults: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BgGradientStart, BgGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.archer_logo),
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Precision Scan for Restricted Apps",
                style = MaterialTheme.typography.headlineMedium,
                color = BrandGold,
                textAlign = TextAlign.Center
            )
            Text(
                "Local analysis. Targeted detection.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )
            Button(onClick = onScan) {
                Text(if (state.isScanning) "Scanning..." else "Scan Now")
            }
            Spacer(Modifier.height(24.dp))
            state.summary?.let { summary ->
                Surface(modifier = Modifier.glassCard().fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        val formatted = state.lastScanAt?.let {
                            DateFormat.getDateTimeInstance().format(Date(it))
                        } ?: "--"
                        Text(
                            "Last scan: $formatted",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrandGold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enabled: ${summary.installedEnabled}")
                            Text("Disabled: ${summary.installedDisabled}")
                            Text("Missing: ${summary.notInstalled}")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onViewResults, modifier = Modifier.align(Alignment.End)) {
                            Text("View Details")
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
