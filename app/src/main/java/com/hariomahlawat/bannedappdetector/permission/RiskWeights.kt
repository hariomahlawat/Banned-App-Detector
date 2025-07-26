package com.hariomahlawat.bannedappdetector.permission

/** Weights used to compute the overall risk score for an app. */
object RiskWeights {
    const val HIGH = 5
    const val MEDIUM = 3
    const val LOW = 1
    const val CHINESE = 3
    const val RATING_PENALTY = 2
    const val NEGATIVE_MULTIPLIER = 10
}
