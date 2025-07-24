package com.hariomahlawat.bannedappdetector.util

import com.hariomahlawat.bannedappdetector.metadata.AppMetadata

/** Numerical signals derived from [AppMetadata]. */
data class RiskSignals(
    val dangerousPermissions: Int,
    val daysSinceUpdate: Long,
    val downloads: Long?
)

class RiskSignalsExtractor {
    fun extract(meta: AppMetadata, now: Long): RiskSignals {
        val perms = meta.permissions ?: emptyArray()
        val dangerous = perms.count { it in RiskScoreCalculator.DANGEROUS_PERMISSIONS }
        val days = meta.lastUpdateTime?.let { (now - it) / RiskScoreCalculator.MILLIS_PER_DAY } ?: 0L
        return RiskSignals(
            dangerousPermissions = dangerous,
            daysSinceUpdate = days,
            downloads = meta.downloads
        )
    }
}

