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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hariomahlawat.bannedappdetector.components.StatusChip
import java.text.DateFormat
import java.util.Date

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    HomeContent(
        state = state,
        onScan = viewModel::onScan
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Banned App Detector",
            style = MaterialTheme.typography.titleLarge
        )
        state.lastScanAt?.let {
            val formatted = DateFormat.getDateTimeInstance().format(Date(it))
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Last scan: $formatted",
                style = MaterialTheme.typography.bodySmall
            )
        }
        state.summary?.let { s ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Monitored: ${s.totalMonitored}  |  Installed: ${s.installedEnabled + s.installedDisabled}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        state.message?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onScan,
            enabled = !state.isScanning,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            if (state.isScanning) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(if (state.isScanning) "Scanning..." else "Scan Now")
        }
        Spacer(Modifier.height(16.dp))
        Divider()
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.results) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = result.meta.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = result.meta.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    StatusChip(status = result.status)
                }
                Divider()
            }
        }
    }
}
