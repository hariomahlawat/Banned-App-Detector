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
    val developerOptionsEnabled: Boolean = false
)

data class PermissionScanSummary(
    val total: Int,
    val highRisk: Int,
    val mediumRisk: Int,
    val lowRisk: Int,
    val chinese: Int
)

@HiltViewModel
class PermissionScanViewModel @Inject constructor(
    private val scanner: PermissionScanner,
    private val io: CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow(PermissionScanState())
    val state: StateFlow<PermissionScanState> = _state.asStateFlow()

    fun runScan() {
        if (_state.value.isScanning) return
        viewModelScope.launch {
            _state.value = PermissionScanState(isScanning = true)
            val results = withContext(io) { scanner.scanInstalledApps() }
            val devOptions = scanner.isDeveloperOptionsEnabled()
            val summary = PermissionScanSummary(
                total = results.size,
                highRisk = results.count { it.highRiskPermissions.isNotEmpty() },
                mediumRisk = results.count {
                    it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isNotEmpty()
                },
                lowRisk = results.count {
                    it.highRiskPermissions.isEmpty() && it.mediumRiskPermissions.isEmpty()
                },
                chinese = results.count { it.chineseOrigin }
            )
            _state.value = PermissionScanState(
                isScanning = false,
                results = results,
                summary = summary,
                developerOptionsEnabled = devOptions
            )
        }
    }
}
