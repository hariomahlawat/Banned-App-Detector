package com.hariomahlawat.bannedappdetector.model


data class MonitoredAppMeta(
    val packageName: String,
    val displayName: String,
    val category: String? = null,
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
    val scannedAt: Long
)

data class SummaryStats(
    val totalMonitored: Int,
    val installedEnabled: Int,
    val installedDisabled: Int,
    val notInstalled: Int
)
