package com.hariomahlawat.bannedappdetector.monitored


import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitoredAppsRepositoryImpl @Inject constructor() : MonitoredAppsRepository {
    private val baseList = listOf(
        MonitoredAppMeta("com.facebook.katana", "Facebook"),
        MonitoredAppMeta("com.facebook.orca", "Messenger"),
        MonitoredAppMeta("com.facebook.lite", "Facebook Lite"),
        MonitoredAppMeta("com.instagram.android", "Instagram"),
        MonitoredAppMeta("com.instagram.lite", "Instagram Lite"),
        MonitoredAppMeta("com.truecaller", "Truecaller")
    )

    override fun getMonitoredApps(): List<MonitoredAppMeta> = baseList
}
