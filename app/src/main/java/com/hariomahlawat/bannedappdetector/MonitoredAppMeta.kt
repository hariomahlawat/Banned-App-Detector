package com.hariomahlawat.bannedappdetector



enum class AppCategory { BANNED, UNWANTED }

data class MonitoredAppMeta(
    val packageName: String,
    val displayName: String,
    val category: AppCategory = AppCategory.BANNED,
    val notes: String? = null
)

enum class MonitoredStatus { INSTALLED_ENABLED, INSTALLED_DISABLED, NOT_INSTALLED }

data class ScanResult(
    val meta: MonitoredAppMeta,
    val status: MonitoredStatus,
    val versionName: String? = null,
    val versionCode: Long? = null,
    val firstInstallTime: Long? = null,
    val lastUpdateTime: Long? = null,
    val scannedAt: Long,
    val riskScore: Int? = null,
    val riskReason: String? = null
)

data class SummaryStats(
    val totalMonitored: Int,
    val installedEnabled: Int,
    val installedDisabled: Int,
    val notInstalled: Int
)
