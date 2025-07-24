package com.hariomahlawat.bannedappdetector.util

/**
 * Very lightweight sentiment analyser used offline. It simply counts occurrences
 * of a few negative keywords and returns the ratio of reviews that contain any
 * of them. In a real implementation this could be replaced with a local LLM or
 * more sophisticated model.
 */
class ReviewSentimentAnalyzer {
    private val negativeKeywords = setOf(
        "bad", "terrible", "poor", "bug", "crash", "slow", "malware", "adware"
    )

    fun negativeRatio(reviews: List<String>): Float {
        if (reviews.isEmpty()) return 0f
        val negative = reviews.count { review ->
            val text = review.lowercase()
            negativeKeywords.any { kw -> kw in text }
        }
        return negative.toFloat() / reviews.size
    }
}
