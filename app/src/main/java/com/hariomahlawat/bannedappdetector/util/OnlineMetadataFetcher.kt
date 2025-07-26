package com.hariomahlawat.bannedappdetector.util

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Fetches rating and reviews for a package from an online service. */
class OnlineMetadataFetcher {
    data class OnlineMetadata(
        val rating: Float?,
        val reviews: List<String>
    )

    /**
     * Attempts to download rating and review data. If the network call fails
     * we return null values. This keeps the app functional offline while
     * allowing richer data when a connection is available.
     */
    fun fetch(packageName: String): OnlineMetadata {
        return try {
            val url = URL("https://example.com/appmeta?pkg=$packageName")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val obj = JSONObject(text)
            val rating = if (obj.has("rating")) obj.getDouble("rating").toFloat() else null
            val reviews = if (obj.has("reviews")) {
                val arr = obj.getJSONArray("reviews")
                MutableList(arr.length()) { i -> arr.getString(i) }
            } else emptyList()
            OnlineMetadata(rating, reviews)
        } catch (e: Exception) {
            // Fallback when offline or the service is unreachable.
            OnlineMetadata(null, emptyList())
        }
    }
}
