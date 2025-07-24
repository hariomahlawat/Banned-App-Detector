package com.hariomahlawat.bannedappdetector.metadata

import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * Raw metadata collected for an installed app. Some fields may be null if the
 * information is unavailable offline (e.g. Play Store rating).
 */
data class AppMetadata(
    val packageName: String,
    val versionName: String?,
    val versionCode: Long?,
    val permissions: Array<String>?,
    val firstInstallTime: Long?,
    val lastUpdateTime: Long?,
    val developerName: String?,
    val rating: Float?,
    val downloads: Long?
)

/** Collects [AppMetadata] from [PackageInfo]. */
class AppMetadataCollector(private val pm: PackageManager) {
    fun collect(pkgInfo: PackageInfo): AppMetadata {
        val appInfo = pkgInfo.applicationInfo
        val packageName = pkgInfo.packageName
        val versionName = pkgInfo.versionName
        val versionCode = pkgInfo.longVersionCode
        val permissions = pkgInfo.requestedPermissions
        val firstInstall = pkgInfo.firstInstallTime
        val lastUpdate = pkgInfo.lastUpdateTime

        // Developer name isn't directly available; use installer package as a crude stand-in
        val developer = pm.getInstallerPackageName(packageName)

        // Ratings and download counts would normally come from Play Store APIs. Offline we have
        // no access, so return null.
        val rating: Float? = null
        val downloads: Long? = null

        return AppMetadata(
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
            permissions = permissions,
            firstInstallTime = firstInstall,
            lastUpdateTime = lastUpdate,
            developerName = developer,
            rating = rating,
            downloads = downloads
        )
    }
}

