package com.hariomahlawat.bannedappdetector.repository


import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import com.hariomahlawat.bannedappdetector.ScanResult
import com.hariomahlawat.bannedappdetector.ThemeSetting
import kotlinx.coroutines.flow.Flow

interface MonitoredAppsRepository {
    fun getMonitoredApps(): List<MonitoredAppMeta>
}

interface ScanResultsRepository {
    fun scanResultsFlow(): Flow<List<ScanResult>>
    suspend fun saveScanResults(results: List<ScanResult>)
    suspend fun latestResults(): List<ScanResult>
}

interface ThemeRepository {
    fun themeFlow(): Flow<ThemeSetting>
    suspend fun setTheme(theme: ThemeSetting)
    suspend fun currentTheme(): ThemeSetting
}
