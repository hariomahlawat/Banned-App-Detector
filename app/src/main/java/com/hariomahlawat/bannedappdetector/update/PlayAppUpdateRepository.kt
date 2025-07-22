package com.hariomahlawat.bannedappdetector.update

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayAppUpdateRepository @Inject constructor(
    private val manager: AppUpdateManager
) : AppUpdateRepository {
    override suspend fun isUpdateAvailable(): Boolean {
        val info = manager.appUpdateInfo.await()
        return info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
    }
}
