package com.hariomahlawat.bannedappdetector

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
fun UpdateDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val uri =
                    ("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID).toUri()
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                onDismiss()
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Later") }
        },
        title = { Text("Update Available") },
        text = { Text("A new version of this app is available on the Play Store.") }
    )
}
