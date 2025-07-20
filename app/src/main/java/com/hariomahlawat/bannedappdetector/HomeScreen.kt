import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
private fun HomeRoot(
    onScan: () -> Unit,
    isScanning: Boolean
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Scan your device to detect monitored apps",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Tap the Scan button below. We will check only the predefined list; nothing leaves your device.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onScan,
            enabled = !isScanning
        ) {
            if (isScanning) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(if (isScanning) "Scanning..." else "Scan Now")
        }
    }
}
