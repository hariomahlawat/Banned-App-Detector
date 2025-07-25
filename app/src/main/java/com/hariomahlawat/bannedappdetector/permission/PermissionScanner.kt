package com.hariomahlawat.bannedappdetector.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.Settings
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Scans installed apps for sensitive permissions and computes a risk score.
 */
class PermissionScanner(private val context: Context) {

    private val pm = context.packageManager
    private val permissionMap: Map<String, PermissionInfo> by lazy { loadPermissionMap() }
    private val trustedApps: List<String> by lazy { loadListFromAsset("trusted_apps.json") }
    private val chinesePublishers: List<String> by lazy { loadListFromAsset("chinese_publishers.json") }

    private val officialInstallers = setOf(
        "com.android.vending",
        "com.google.android.packageinstaller",
        "com.android.packageinstaller",
        "com.sec.android.app.samsungapps"
    )

    fun scanInstalledApps(): List<AppRiskReport> {
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            .filter { pkg ->
                val info = pkg.applicationInfo
                info != null &&
                    info.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
                    info.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0
            }
        return packages.map { pkg ->
            val perms = pkg.requestedPermissions?.toList() ?: emptyList()
            val app = AppPermissions(
                appName = pkg.applicationInfo?.loadLabel(pm).toString(),
                packageName = pkg.packageName,
                permissions = perms
            )
            analyzeRisk(app)
        }
    }

    private fun analyzeRisk(app: AppPermissions): AppRiskReport {
        val high = app.permissions.filter { permissionMap[it]?.risk == "HIGH" }
        val medium = app.permissions.filter { permissionMap[it]?.risk == "MEDIUM" }
        val low = app.permissions.filter { permissionMap[it]?.risk == "LOW" }
        val chinese = isChineseOrigin(app.packageName)
        val score = computeRiskScore(high, medium, low, app.packageName, chinese)

        val background = app.permissions.filter { permissionMap[it]?.type == "PASSIVE" }
        val sideloaded = isSideloaded(app.packageName)
        val modded = looksLikeModApp(app.appName, app.packageName)

        return AppRiskReport(
            app = app,
            highRiskPermissions = high,
            mediumRiskPermissions = medium,
            lowRiskPermissions = low,
            chineseOrigin = chinese,
            riskScore = score,
            sideloaded = sideloaded,
            modApp = modded,
            backgroundPermissions = background
        )
    }

    private fun computeRiskScore(high: List<String>, medium: List<String>, low: List<String>, packageName: String, chinese: Boolean): Int {
        var score = high.size * 5 + medium.size * 3 + low.size
        if (chinese) score += 3
        if (packageName in trustedApps) score = kotlin.math.max(score - 5, 1)
        return score
    }

    private fun isChineseOrigin(packageName: String): Boolean {
        return chinesePublishers.any { packageName.startsWith(it) }
    }

    private fun loadPermissionMap(): Map<String, PermissionInfo> {
        val json = assetToString("permissions.json")
        val obj = JSONObject(json)
        val map = mutableMapOf<String, PermissionInfo>()
        obj.keys().forEach { key ->
            val p = obj.getJSONObject(key)
            map[key] = PermissionInfo(p.getString("risk"), p.getString("type"))
        }
        return map
    }

    private fun loadListFromAsset(name: String): List<String> {
        val json = assetToString(name)
        val array = org.json.JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list += array.getString(i)
        }
        return list
    }

    private fun isSideloaded(packageName: String): Boolean {
        val installer = pm.getInstallerPackageName(packageName)
        return installer == null || installer !in officialInstallers
    }

    private fun looksLikeModApp(appName: String, packageName: String): Boolean {
        val text = (appName + packageName).lowercase()
        return listOf("mod", "patched", "crack", "hack").any { it in text }
    }

    fun isDeveloperOptionsEnabled(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0
        } catch (_: Exception) { false }
    }

    private fun assetToString(name: String): String {
        context.assets.open(name).use { stream ->
            val reader = BufferedReader(InputStreamReader(stream))
            return reader.readText()
        }
    }

    fun openAppSettings(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun promptUninstall(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

/** Holds permissions of an installed app. */
data class AppPermissions(
    val appName: String,
    val packageName: String,
    val permissions: List<String>
)

/** Report with categorized permissions and risk score. */
data class AppRiskReport(
    val app: AppPermissions,
    val highRiskPermissions: List<String>,
    val mediumRiskPermissions: List<String>,
    val lowRiskPermissions: List<String>,
    val chineseOrigin: Boolean,
    val riskScore: Int,
    val sideloaded: Boolean,
    val modApp: Boolean,
    val backgroundPermissions: List<String>
)
