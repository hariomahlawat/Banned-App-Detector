package com.hariomahlawat.bannedappdetector.permission

/**
 * Utility to check if a package belongs to a Chinese-origin publisher
 * based on package name prefixes.
 */
class ChineseOriginIdentifier(private val prefixes: List<String>) {
    fun isChinese(packageName: String): Boolean {
        return prefixes.any { packageName.startsWith(it) }
    }
}
