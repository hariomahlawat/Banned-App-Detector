package com.hariomahlawat.bannedappdetector.usecase

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
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

    /** runs the scan on the supplied IO dispatcher */
    suspend operator fun invoke(): List<ScanResult> = withContext(io) {

        val now = clock()

        /* 1. Capture (PackageInfo, ApplicationInfo?) pairs --------------- */
        val installed: List<Pair<PackageInfo, ApplicationInfo?>> =
            pm.getInstalledPackages(0) /* API‑33+: use flagsOf(0L) */       // :contentReference[oaicite:0]{index=0}
                .map { pkg -> pkg to pkg.applicationInfo }                  // nullable by design :contentReference[oaicite:1]{index=1}

        /* 2. Build result list ------------------------------------------- */
        val results = monitoredAppsRepository.getMonitoredApps().map { meta ->

            /* find first installed app whose *label* matches displayName */
            val match = installed.find { (_, info) ->
                info != null &&                                           // guard nullability :contentReference[oaicite:2]{index=2}
                        pm.getApplicationLabel(info).toString()
                            .equals(meta.displayName, ignoreCase = true)
            }

            if (match != null) {
                val (pkgInfo, appInfo) = match          // appInfo is non‑null now
                val status = if (appInfo!!.enabled)      // safe !! after check
                    MonitoredStatus.INSTALLED_ENABLED
                else
                    MonitoredStatus.INSTALLED_DISABLED

                ScanResult(
                    meta            = meta,
                    status          = status,
                    versionName     = pkgInfo.versionName,
                    versionCode     = pkgInfo.longVersionCode,
                    firstInstallTime= pkgInfo.firstInstallTime,
                    lastUpdateTime  = pkgInfo.lastUpdateTime,
                    scannedAt       = now
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
