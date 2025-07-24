package com.hariomahlawat.bannedappdetector.update

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallException
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayAppUpdateRepository @Inject constructor(
    private val manager: AppUpdateManager
) : AppUpdateRepository {
    override suspend fun isUpdateAvailable(): Boolean {
        return try {
            val info = manager.appUpdateInfo.await()
            info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        } catch (_: InstallException) {
            false
        }
    }
}
