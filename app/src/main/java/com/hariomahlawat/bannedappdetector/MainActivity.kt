package com.hariomahlawat.bannedappdetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hariomahlawat.bannedappdetector.permission.PermissionRiskScreen
import com.hariomahlawat.bannedappdetector.ui.theme.BannedAppDetectorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val vm: ThemeViewModel = hiltViewModel()
            val theme by vm.theme.collectAsState()
            BannedAppDetectorTheme(theme) {
                AppNavigation(theme = theme, onToggleTheme = vm::toggleTheme)
            }
        }
    }
}

@Composable
fun AppNavigation(theme: ThemeSetting, onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("home") {
            val dark = theme == ThemeSetting.DARK ||
                       (theme == ThemeSetting.SYSTEM &&
                        androidx.compose.foundation.isSystemInDarkTheme())
            HomeScreen(
                onViewResults    = { navController.navigate("results") },
                onViewBannedApps = { navController.navigate("bannedApps") },
                onAiScan        = { navController.navigate("aiScan") },
                onToggleTheme    = onToggleTheme,
                dark             = dark
            )
        }
        composable("results") {
            val dark = theme == ThemeSetting.DARK ||
                       (theme == ThemeSetting.SYSTEM &&
                        androidx.compose.foundation.isSystemInDarkTheme())
            ResultsScreen(
                onBack = { navController.popBackStack() },
                dark   = dark
            )
        }
        composable("bannedApps") {
            val dark = theme == ThemeSetting.DARK ||
                       (theme == ThemeSetting.SYSTEM &&
                        androidx.compose.foundation.isSystemInDarkTheme())
            BannedAppsScreen(
                onBack = { navController.popBackStack() },
                dark   = dark
            )
        }
        composable("aiScan") {
            val dark = theme == ThemeSetting.DARK ||
                       (theme == ThemeSetting.SYSTEM &&
                        androidx.compose.foundation.isSystemInDarkTheme())
            PermissionRiskScreen(
                onBack = { navController.popBackStack() },
                dark   = dark
            )
        }
    }
}
