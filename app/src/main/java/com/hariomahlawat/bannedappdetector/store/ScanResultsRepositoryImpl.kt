package com.hariomahlawat.bannedappdetector.store



import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import com.hariomahlawat.bannedappdetector.MonitoredStatus
import com.hariomahlawat.bannedappdetector.ScanResult
import com.hariomahlawat.bannedappdetector.repository.ScanResultsRepository
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val DS_NAME = "scan_results"
private val Context.dataStore by preferencesDataStore(DS_NAME)
private val KEY_JSON = stringPreferencesKey("results_json")

@Singleton
class ScanResultsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ScanResultsRepository {

    override fun scanResultsFlow(): Flow<List<ScanResult>> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_JSON]?.let { decode(it) } ?: emptyList()
        }

    override suspend fun saveScanResults(results: List<ScanResult>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_JSON] = encode(results)
        }
    }

    override suspend fun latestResults(): List<ScanResult> {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_JSON]?.let { decode(it) } ?: emptyList()
    }

    private fun encode(list: List<ScanResult>): String {
        val arr = JSONArray()
        list.forEach { r ->
            val o = JSONObject().apply {
                put("pkg", r.meta.packageName)
                put("name", r.meta.displayName)
                put("status", r.status.name)
                put("verName", r.versionName)
                put("verCode", r.versionCode)
                put("first", r.firstInstallTime)
                put("last", r.lastUpdateTime)
                put("at", r.scannedAt)
                put("risk", r.riskScore)
                put("reason", r.riskReason)
            }
            arr.put(o)
        }
        return arr.toString()
    }

    private fun decode(json: String): List<ScanResult> {
        val arr = JSONArray(json)
        val out = mutableListOf<ScanResult>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val meta = MonitoredAppMeta(o.getString("pkg"), o.getString("name"))
            val status = MonitoredStatus.valueOf(o.getString("status"))
            out += ScanResult(
                meta = meta,
                status = status,
                versionName = o.optString("verName", null),
                versionCode = if (o.isNull("verCode")) null else o.getLong("verCode"),
                firstInstallTime = if (o.isNull("first")) null else o.getLong("first"),
                lastUpdateTime = if (o.isNull("last")) null else o.getLong("last"),
                scannedAt = o.getLong("at"),
                riskScore = if (o.isNull("risk")) null else o.getInt("risk"),
                riskReason = o.optString("reason", null)
            )
        }
        return out
    }
}
