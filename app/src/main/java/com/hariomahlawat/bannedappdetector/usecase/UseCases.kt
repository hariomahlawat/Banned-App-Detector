package com.hariomahlawat.bannedappdetector.usecase

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import com.hariomahlawat.bannedappdetector.util.RiskScoreCalculator
import com.hariomahlawat.bannedappdetector.MonitoredStatus
import com.hariomahlawat.bannedappdetector.ScanResult
import com.hariomahlawat.bannedappdetector.SummaryStats
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import com.hariomahlawat.bannedappdetector.repository.ScanResultsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Scans the device for apps that the repository marks as “monitored”.
 * Handles nullable `applicationInfo` safely to avoid NPE / type‑mismatch.
 */
class ScanMonitoredAppsUseCase(
    private val monitoredAppsRepository: MonitoredAppsRepository,
    private val scanResultsRepository: ScanResultsRepository,
    private val pm: PackageManager,
    private val io: CoroutineDispatcher,
    private val clock: () -> Long
) {
    private val riskScorer = RiskScoreCalculator(pm)

    /** runs the scan on the supplied IO dispatcher */
    suspend operator fun invoke(includeUnwanted: Boolean): List<ScanResult> = withContext(io) {

        val now = clock()

        /* 1. Capture (PackageInfo, ApplicationInfo?) pairs --------------- */
        val installed: List<Pair<PackageInfo, ApplicationInfo?>> =
            pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                /* API‑33+: use flagsOf(PackageManager.GET_PERMISSIONS.toLong()) */
                .map { pkg -> pkg to pkg.applicationInfo }                  // nullable by design

        /* 2. Build result list ------------------------------------------- */
        val results = monitoredAppsRepository.getMonitoredApps(includeUnwanted).map { meta ->

            /* find first installed app whose package name matches */
            val match = installed.find { (pkgInfo, _) ->
                pkgInfo.packageName.equals(meta.packageName, ignoreCase = true)
            }

            if (match != null) {
                val (pkgInfo, appInfo) = match          // appInfo is non‑null now
                val status = if (appInfo!!.enabled)      // safe !! after check
                    MonitoredStatus.INSTALLED_ENABLED
                else
                    MonitoredStatus.INSTALLED_DISABLED

                val (score, reason) = riskScorer.score(pkgInfo, now)
                ScanResult(
                    meta            = meta,
                    status          = status,
                    versionName     = pkgInfo.versionName,
                    versionCode     = pkgInfo.longVersionCode,
                    firstInstallTime= pkgInfo.firstInstallTime,
                    lastUpdateTime  = pkgInfo.lastUpdateTime,
                    scannedAt       = now,
                    riskScore       = score,
                    riskReason      = reason
                )
            } else {
                ScanResult(meta, MonitoredStatus.NOT_INSTALLED, scannedAt = now)
            }
        }

        /* 3. Persist & return ------------------------------------------- */
        scanResultsRepository.saveScanResults(results)
        results
    }
}

/* ---------------------------- helpers -------------------------------- */

class GetScanResultsFlowUseCase(
    private val repo: ScanResultsRepository
) {
    operator fun invoke() = repo.scanResultsFlow()
}

class ComputeSummaryStatsUseCase {
    operator fun invoke(results: List<ScanResult>): SummaryStats =
        SummaryStats(
            totalMonitored    = results.size,
            installedEnabled  = results.count { it.status == MonitoredStatus.INSTALLED_ENABLED },
            installedDisabled = results.count { it.status == MonitoredStatus.INSTALLED_DISABLED },
            notInstalled      = results.count { it.status == MonitoredStatus.NOT_INSTALLED }
        )
}
