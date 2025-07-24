package com.hariomahlawat.bannedappdetector.util

import com.hariomahlawat.bannedappdetector.metadata.AppMetadata
import org.junit.Assert.assertTrue
import org.junit.Test

class RiskScoreCalculatorTest {
    @Test
    fun score_increases_with_negative_reviews() {
        val meta = AppMetadata(
            packageName = "test",
            versionName = "1",
            versionCode = 1L,
            permissions = arrayOf("android.permission.CAMERA"),
            firstInstallTime = null,
            lastUpdateTime = null,
            developerName = "dev",
            rating = 2.5f,
            downloads = 5000,
            publisherName = "pub",
            reviews = listOf("bad app", "terrible")
        )
        val (score, _) = RiskScoreCalculator().score(meta, now = 0L)
        assertTrue("score should reflect negative reviews", score > 0)
    }
}
