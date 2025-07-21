package com.hariomahlawat.bannedappdetector.monitored


import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import com.hariomahlawat.bannedappdetector.bannedAppsList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitoredAppsRepositoryImpl @Inject constructor() : MonitoredAppsRepository {
    private val baseList = bannedAppsList.map { name ->
        MonitoredAppMeta(name, name)
    }

    override fun getMonitoredApps(): List<MonitoredAppMeta> = baseList
}
