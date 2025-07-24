package com.hariomahlawat.bannedappdetector.update

interface AppUpdateRepository {
    suspend fun isUpdateAvailable(): Boolean
}
