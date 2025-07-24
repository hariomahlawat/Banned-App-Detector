package com.hariomahlawat.bannedappdetector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hariomahlawat.bannedappdetector.usecase.ComputeSummaryStatsUseCase
import com.hariomahlawat.bannedappdetector.usecase.GetScanResultsFlowUseCase
import com.hariomahlawat.bannedappdetector.usecase.ScanMonitoredAppsUseCase
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import com.hariomahlawat.bannedappdetector.MonitoredStatus
import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isScanning: Boolean = false,
    val progress: Float = 0f,
    val foundCount: Int = 0,
    val results: List<ScanResult> = emptyList(),
    val summary: SummaryStats? = null,
    val lastScanAt: Long? = null,
    val message: String? = null,
    val showMonitoredDialog: Boolean = false,
    val monitoredApps: List<MonitoredAppMeta> = emptyList(),
    val includeUnwanted: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scan: ScanMonitoredAppsUseCase,
    private val resultsFlow: GetScanResultsFlowUseCase,
    private val summaryUseCase: ComputeSummaryStatsUseCase,
    private val monitoredRepo: MonitoredAppsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeUiState(
            monitoredApps = monitoredRepo.getMonitoredApps(includeUnwanted = true),
            includeUnwanted = true
        )
    )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        resultsFlow().onEach { list ->
            _state.update { current ->
                if (current.isScanning) current else {
                    val installed = list.filter { it.status != MonitoredStatus.NOT_INSTALLED }
                    current.copy(
                        results = installed,
                        summary = summaryUseCase(list),
                        lastScanAt = list.firstOrNull()?.scannedAt
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onScan() {
        if (_state.value.isScanning) return
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, message = null, progress = 0f, foundCount = 0, results = emptyList()) }
            val allResults = scan(_state.value.includeUnwanted)
            val installed = allResults.filter { it.status != MonitoredStatus.NOT_INSTALLED }
            val summary = summaryUseCase(allResults)
            _state.update {
                it.copy(
                    results = installed,
                    summary = summary,
                    lastScanAt = allResults.firstOrNull()?.scannedAt
                )
            }
        }
    }

    fun onScanAnimationFinished() {
        val summary = _state.value.summary
        _state.update { current ->
            current.copy(
                isScanning = false,
                progress = 1f,
                foundCount = current.results.size,
                message = summary?.let {
                    "Scan complete: ${it.installedEnabled} enabled, ${it.installedDisabled} disabled"
                }
            )
        }
    }

    fun showMonitored(show: Boolean) { _state.update { it.copy(showMonitoredDialog = show) } }

    fun setIncludeUnwanted(value: Boolean) { _state.update { it.copy(includeUnwanted = value) } }

    fun dismissMessage() { _state.update { it.copy(message = null) } }
}
