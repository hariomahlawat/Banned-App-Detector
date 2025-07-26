package com.hariomahlawat.bannedappdetector.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/** Holds state for the AI permission scan UI. */
data class PermissionScanState(
    val isScanning: Boolean = false,
    val results: List<AppRiskReport> = emptyList(),
    val summary: PermissionScanSummary? = null,
    val developerOptionsEnabled: Boolean = false,
    val buckets: AppRiskBuckets = AppRiskBuckets(),
    val reviewInsights: ReviewInsights? = null,
    val error: String? = null
)

data class PermissionScanSummary(
    val total: Int,
    val highRisk: Int,
    val mediumRisk: Int,
    val lowRisk: Int,
    val chinese: Int
)

data class AppRiskBuckets(
    val chinese: List<AppRiskReport> = emptyList(),
    val high: List<AppRiskReport> = emptyList(),
    val medium: List<AppRiskReport> = emptyList(),
    val low: List<AppRiskReport> = emptyList(),
    val sideloaded: List<AppRiskReport> = emptyList(),
    val modded: List<AppRiskReport> = emptyList(),
    val background: List<AppRiskReport> = emptyList()
)

data class ReviewInsights(
    val avgRating: Float,
    val lowRated: List<AppRiskReport>,
    val offenders: List<AppRiskReport>,
    val reviewedCount: Int,
    val offline: Boolean
)

@HiltViewModel
class PermissionScanViewModel @Inject constructor(
    private val scanner: PermissionScanner,
    private val io: CoroutineDispatcher
) : ViewModel(), AppRiskActionHandler by scanner {
    private val _state = MutableStateFlow(PermissionScanState())
    val state: StateFlow<PermissionScanState> = _state.asStateFlow()

    fun runScan() {
        if (_state.value.isScanning) return
        viewModelScope.launch {
            _state.value = PermissionScanState(isScanning = true)
            try {
                val results = withContext(io) { scanner.scanInstalledApps() }
                val devOptions = scanner.isDeveloperOptionsEnabled()

                val buckets = AppRiskBuckets(
                    chinese = results.filter { it.chineseOrigin },
                    high = results.filter { !it.chineseOrigin && it.highRiskPermissions.isNotEmpty() },
                    medium = results.filter { !it.chineseOrigin && it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isNotEmpty() },
                    low = results.filter { !it.chineseOrigin && it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isEmpty() },
                    sideloaded = results.filter { it.sideloaded },
                    modded = results.filter { it.modApp },
                    background = results.filter { it.backgroundPermissions.isNotEmpty() }
                )

                val summary = PermissionScanSummary(
                    total = results.size,
                    highRisk = buckets.high.size,
                    mediumRisk = buckets.medium.size,
                    lowRisk = buckets.low.size,
                    chinese = buckets.chinese.size
                )

                val ratings = results.mapNotNull { it.rating }
                val avgRating = if (ratings.isNotEmpty()) ratings.average().toFloat() else 0f
                val lowRated = results.filter { it.rating != null && it.rating < 3.5f }
                val reviewedCount = results.count { it.reviewCount > 0 }
                val offenders = results.filter { it.negativeReviewRatio > 0.4f }
                    .sortedByDescending { it.negativeReviewRatio }
                val offline = results.any { it.fromCache }
                val insights = ReviewInsights(avgRating, lowRated, offenders, reviewedCount, offline)

                _state.value = PermissionScanState(
                    isScanning = false,
                    results = results,
                    summary = summary,
                    developerOptionsEnabled = devOptions,
                    buckets = buckets,
                    reviewInsights = insights
                )
            } catch (e: Exception) {
                val devOptions = scanner.isDeveloperOptionsEnabled()
                _state.value = PermissionScanState(
                    isScanning = false,
                    developerOptionsEnabled = devOptions,
                    error = e.message
                )
            }
        }
    }
}
