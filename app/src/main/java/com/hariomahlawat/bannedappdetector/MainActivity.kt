package com.hariomahlawat.bannedappdetector

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.animation.doOnEnd
import dagger.hilt.android.AndroidEntryPoint
import com.hariomahlawat.bannedappdetector.ui.theme.BannedAppDetectorTheme
import com.hariomahlawat.bannedappdetector.SecurityGridView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BannedAppDetectorTheme {
                AppNavigation()
            }
        }
        val content = findViewById<View>(android.R.id.content)
        content.alpha = 0f

        splash.setOnExitAnimationListener { provider ->
            val parent = provider.view.parent as ViewGroup
            val grid = SecurityGridView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            parent.addView(grid)

            val width = provider.view.width.toFloat()
            val slide = ObjectAnimator.ofFloat(grid, View.TRANSLATION_X, -width, width).apply {
                duration = 250
            }
            val fadeOut = ObjectAnimator.ofFloat(grid, View.ALPHA, grid.alpha, 0f).apply {
                startDelay = 250
                duration = 150
            }
            val contentFade = ObjectAnimator.ofFloat(content, View.ALPHA, 0f, 1f).apply {
                startDelay = 250
                duration = 150
            }
            AnimatorSet().apply {
                playTogether(slide, fadeOut, contentFade)
                doOnEnd {
                    parent.removeView(grid)
                    provider.remove()
                }
                start()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onViewResults    = { navController.navigate("results") },
                onViewBannedApps = { navController.navigate("bannedApps") }
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
