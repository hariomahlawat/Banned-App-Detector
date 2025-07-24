package com.hariomahlawat.bannedappdetector.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.Manifest

/**
 * Simple heuristic based risk scoring for installed apps.
 * Scores are from 0-100 based on requested dangerous permissions
 * and how long ago the app was updated.
 */
class RiskScoreCalculator(private val pm: PackageManager) {

    fun score(pkgInfo: PackageInfo, now: Long): Pair<Int, String> {
        var score = 0
        val reasons = mutableListOf<String>()

        val perms = pkgInfo.requestedPermissions
        if (perms != null) {
            val dangerous = perms.count { it in DANGEROUS_PERMISSIONS }
            if (dangerous > 0) {
                score += dangerous * 10
                reasons += "$dangerous sensitive permissions"
            }
        }

        val lastUpdate = pkgInfo.lastUpdateTime
        if (lastUpdate > 0) {
            val days = (now - lastUpdate) / MILLIS_PER_DAY
            if (days > OLD_THRESHOLD_DAYS) {
                score += 10
                reasons += "not updated for ${days/30} months"
            }
        }

        val final = score.coerceIn(0, 100)
        val reason = if (reasons.isEmpty()) "low risk" else reasons.joinToString("; ")
        return final to reason
    }

    companion object {
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
        private const val OLD_THRESHOLD_DAYS = 180

        private val DANGEROUS_PERMISSIONS = setOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }
}
