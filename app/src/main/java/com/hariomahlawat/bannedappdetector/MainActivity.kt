package com.hariomahlawat.bannedappdetector

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.hariomahlawat.bannedappdetector.ui.theme.BannedAppDetectorTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BannedAppDetectorTheme {
                HomeScreen()
            }
        }
    }
}
