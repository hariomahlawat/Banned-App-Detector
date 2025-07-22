package com.hariomahlawat.bannedappdetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.hariomahlawat.bannedappdetector.ui.theme.BannedAppDetectorTheme
import com.hariomahlawat.bannedappdetector.ThemeViewModel
import com.hariomahlawat.bannedappdetector.ThemeSetting

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
            HomeScreen(
                onViewResults    = { navController.navigate("results") },
                onViewBannedApps = { navController.navigate("bannedApps") },
                onToggleTheme    = onToggleTheme,
                dark             = theme == ThemeSetting.DARK ||
                                   (theme == ThemeSetting.SYSTEM &&
                                    androidx.compose.foundation.isSystemInDarkTheme())
            )
        }
        composable("results") {
            ResultsScreen(onBack = { navController.popBackStack() })
        }
        composable("bannedApps") {
            BannedAppsScreen(onBack = { navController.popBackStack() })
        }
    }
}
