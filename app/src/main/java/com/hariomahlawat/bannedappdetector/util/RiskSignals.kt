package com.hariomahlawat.bannedappdetector.util

import com.hariomahlawat.bannedappdetector.metadata.AppMetadata

/** Numerical signals derived from [AppMetadata]. */
data class RiskSignals(
    val dangerousPermissions: Int,
    val daysSinceUpdate: Long,
    val rating: Float?,
    val downloads: Long?,
    val developerReputation: Float,
    val publisherReputation: Float,
    val negativeReviewRatio: Float?
)

class RiskSignalsExtractor {
    fun extract(meta: AppMetadata, now: Long): RiskSignals {
        val perms = meta.permissions ?: emptyArray()
        val dangerous = perms.count { it in RiskScoreCalculator.DANGEROUS_PERMISSIONS }
        val days = meta.lastUpdateTime?.let { (now - it) / RiskScoreCalculator.MILLIS_PER_DAY } ?: 0L
        // Developer reputation and popularity require external data. We default to neutral values.
        val reputation = if (meta.developerName != null) 0.5f else 0.0f
        val publisherRep = if (meta.publisherName != null) 0.5f else 0.0f

        val reviewRatio = meta.reviews?.let {
            ReviewSentimentAnalyzer().negativeRatio(it)
        }
        return RiskSignals(
            dangerousPermissions = dangerous,
            daysSinceUpdate = days,
            rating = meta.rating,
            downloads = meta.downloads,
            developerReputation = reputation,
            publisherReputation = publisherRep,
            negativeReviewRatio = reviewRatio
        )
    }
}

