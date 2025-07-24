package com.hariomahlawat.bannedappdetector.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewSentimentAnalyzerTest {
    @Test
    fun negative_ratio_computed() {
        val reviews = listOf(
            "Great app",
            "Bad experience, very slow",
            "Terrible design",
            "Works fine"
        )
        val ratio = ReviewSentimentAnalyzer().negativeRatio(reviews)
        assertEquals(0.5f, ratio)
    }
}
