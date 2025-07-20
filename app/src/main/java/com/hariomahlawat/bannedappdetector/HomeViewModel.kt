package com.hariomahlawat.bannedappdetector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hariomahlawat.bannedappdetector.usecase.ComputeSummaryStatsUseCase
import com.hariomahlawat.bannedappdetector.usecase.GetScanResultsFlowUseCase
import com.hariomahlawat.bannedappdetector.usecase.ScanMonitoredAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isScanning: Boolean = false,
    val results: List<ScanResult> = emptyList(),
    val summary: SummaryStats? = null,
    val lastScanAt: Long? = null,
    val message: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scan: ScanMonitoredAppsUseCase,
    private val resultsFlow: GetScanResultsFlowUseCase,
    private val summaryUseCase: ComputeSummaryStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        resultsFlow().onEach { list ->
            _state.update {
                it.copy(
                    results = list,
                    summary = summaryUseCase(list),
                    lastScanAt = list.firstOrNull()?.scannedAt
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onScan() {
        if (_state.value.isScanning) return
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, message = null) }
            val newResults = scan()
            val summary = summaryUseCase(newResults)
            _state.update {
                it.copy(
                    isScanning = false,
                    message = "Scan complete: ${summary.installedEnabled} enabled, ${summary.installedDisabled} disabled"
                )
            }
        }
    }

    fun dismissMessage() { _state.update { it.copy(message = null) } }
}
