package com.hariomahlawat.bannedappdetector.util

/** Extracts the most common keywords from a list of review strings. */
class KeywordExtractor {
    private val stopwords = setOf(
        "the", "and", "with", "from", "that", "this", "have", "for", "are", "was",
        "were", "will", "would", "should", "could", "there", "their", "about",
        "android", "phone", "app", "apps", "please", "issue", "problem"
    )

    fun topKeywords(reviews: List<String>, max: Int = 3): List<String> {
        if (reviews.isEmpty()) return emptyList()
        val counts = mutableMapOf<String, Int>()
        for (review in reviews) {
            val words = review.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }
            for (w in words) {
                if (w !in stopwords && w.length > 3) {
                    counts[w] = counts.getOrDefault(w, 0) + 1
                }
            }
        }
        return counts.entries.sortedByDescending { it.value }.take(max).map { it.key }
    }
}
