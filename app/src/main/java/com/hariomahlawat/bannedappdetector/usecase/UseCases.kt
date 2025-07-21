package com.hariomahlawat.bannedappdetector.usecase

import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.hariomahlawat.bannedappdetector.MonitoredStatus
import com.hariomahlawat.bannedappdetector.ScanResult
import com.hariomahlawat.bannedappdetector.SummaryStats
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import com.hariomahlawat.bannedappdetector.repository.ScanResultsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ScanMonitoredAppsUseCase(
    private val monitoredAppsRepository: MonitoredAppsRepository,
    private val scanResultsRepository: ScanResultsRepository,
    private val pm: PackageManager,
    private val io: CoroutineDispatcher,
    private val clock: () -> Long
) {
    suspend operator fun invoke(): List<ScanResult> = withContext(io) {
        val now = clock()
        val installed: List<Pair<PackageInfo, ApplicationInfo>> =
            pm.getInstalledPackages(0).map { pkg ->
                pkg to pkg.applicationInfo
            }

        val results = monitoredAppsRepository.getMonitoredApps().map { meta ->
            val match = installed.find { (_, info) ->
                val label = pm.getApplicationLabel(info).toString()
                label.equals(meta.displayName, ignoreCase = true)
            }

            if (match != null) {
                val (pkgInfo, appInfo) = match
                val status = if (appInfo.enabled) {
                    MonitoredStatus.INSTALLED_ENABLED
                } else {
                    MonitoredStatus.INSTALLED_DISABLED
                }
                ScanResult(
                    meta = meta,
                    status = status,
                    versionName = pkgInfo.versionName,
                    versionCode = pkgInfo.longVersionCode,
                    firstInstallTime = pkgInfo.firstInstallTime,
                    lastUpdateTime = pkgInfo.lastUpdateTime,
                    scannedAt = now
                )
            } else {
                ScanResult(meta, MonitoredStatus.NOT_INSTALLED, scannedAt = now)
            }
        }
        scanResultsRepository.saveScanResults(results)
        results
    }
}

class GetScanResultsFlowUseCase(
    private val repo: ScanResultsRepository
) {
    operator fun invoke() = repo.scanResultsFlow()
}

class ComputeSummaryStatsUseCase {
    operator fun invoke(results: List<ScanResult>): SummaryStats {
        val installedEnabled = results.count { it.status == MonitoredStatus.INSTALLED_ENABLED }
        val installedDisabled = results.count { it.status == MonitoredStatus.INSTALLED_DISABLED }
        val notInstalled = results.count { it.status == MonitoredStatus.NOT_INSTALLED }
        return SummaryStats(
            totalMonitored = results.size,
            installedEnabled = installedEnabled,
            installedDisabled = installedDisabled,
            notInstalled = notInstalled
        )
    }
}
