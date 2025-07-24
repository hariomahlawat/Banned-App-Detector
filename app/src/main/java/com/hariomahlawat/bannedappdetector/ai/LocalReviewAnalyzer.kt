package com.hariomahlawat.bannedappdetector.ai

/**
 * Naive local analysis of user reviews and publisher information.
 * In a real implementation this could call an on-device language model.
 */
class LocalReviewAnalyzer {
    private val negativeKeywords = listOf(
        "malware", "spyware", "virus", "steal", "scam", "hijack", "adware"
    )

    /**
     * @param reviews recent review texts, may be empty
     * @param rating average rating from the store
     * @param developer package installer or publisher
     * @return Pair of additional risk score and explanation string if any
     */
    fun analyze(reviews: List<String>, rating: Float?, developer: String?): Pair<Float, String?> {
        var score = 0f
        val reasons = mutableListOf<String>()

        val lowerReviews = reviews.map { it.lowercase() }
        val hits = lowerReviews.count { r -> negativeKeywords.any { it in r } }
        if (hits > 0) {
            score += hits * 5f
            reasons += "$hits security complaints"
        }

        rating?.let {
            if (it < 3.5f) {
                score += (3.5f - it) * 5f
                reasons += "low rating"
            }
        }

        developer?.let {
            if (!TRUSTED_PUBLISHERS.any { p -> it.contains(p, ignoreCase = true) }) {
                score += 5f
                reasons += "untrusted publisher"
            }
        }

        val reason = if (reasons.isEmpty()) null else reasons.joinToString("; ")
        return score to reason
    }

    companion object {
        val TRUSTED_PUBLISHERS = listOf("Google", "Mozilla", "Samsung")
    }
}
