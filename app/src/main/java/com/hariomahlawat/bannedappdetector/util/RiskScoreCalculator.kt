package com.hariomahlawat.bannedappdetector.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.Manifest
import com.hariomahlawat.bannedappdetector.metadata.AppMetadata

/**
 * Simple heuristic based risk scoring for installed apps.
 * Scores are from 0-100 based on requested dangerous permissions
 * and how long ago the app was updated.
 */
class RiskScoreCalculator(private val pm: PackageManager) {

    private val extractor = RiskSignalsExtractor()

    fun score(pkgInfo: PackageInfo, now: Long): Pair<Int, String> {
        val meta = AppMetadataCollector(pm).collect(pkgInfo)
        return score(meta, now)
    }

    /** Score an app based on extracted [RiskSignals]. */
    fun score(meta: AppMetadata, now: Long): Pair<Int, String> {
        val signals = extractor.extract(meta, now)

        var score = 0f
        val reasons = mutableListOf<String>()

        if (signals.dangerousPermissions > 0) {
            score += signals.dangerousPermissions * 10
            reasons += "${signals.dangerousPermissions} sensitive permissions"
        }

        if (signals.daysSinceUpdate > OLD_THRESHOLD_DAYS) {
            score += 10f
            reasons += "not updated for ${signals.daysSinceUpdate/30} months"
        }

        signals.rating?.let {
            if (it < 3.0f) {
                score += (3.0f - it) * 10
                reasons += "low user rating"
            }
        }

        // downloads: if low popularity, slightly increase score
        signals.downloads?.let {
            if (it < 10_000) {
                score += 5f
                reasons += "low install count"
            }
        }

        if (signals.developerReputation < 0.3f) {
            score += 5f
            reasons += "unverified developer"
        }

        val final = score.toInt().coerceIn(0, 100)
        val reason = if (reasons.isEmpty()) "low risk" else reasons.joinToString("; ")
        return final to reason
    }

    companion object {
        internal const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
        internal const val OLD_THRESHOLD_DAYS = 180

        internal val DANGEROUS_PERMISSIONS = setOf(
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
