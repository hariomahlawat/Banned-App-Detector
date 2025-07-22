package com.hariomahlawat.bannedappdetector.usecase

import com.hariomahlawat.bannedappdetector.update.AppUpdateRepository

class CheckForAppUpdateUseCase(
    private val repo: AppUpdateRepository
) {
    suspend operator fun invoke(): Boolean = repo.isUpdateAvailable()
}
