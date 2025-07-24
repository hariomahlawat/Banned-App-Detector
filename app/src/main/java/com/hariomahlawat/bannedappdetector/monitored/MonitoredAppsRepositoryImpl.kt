package com.hariomahlawat.bannedappdetector.monitored


import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import com.hariomahlawat.bannedappdetector.bannedAppsList
import com.hariomahlawat.bannedappdetector.unwantedAppsList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitoredAppsRepositoryImpl @Inject constructor() : MonitoredAppsRepository {
    override fun getMonitoredApps(includeUnwanted: Boolean): List<MonitoredAppMeta> =
        if (includeUnwanted) bannedAppsList + unwantedAppsList else bannedAppsList
}
