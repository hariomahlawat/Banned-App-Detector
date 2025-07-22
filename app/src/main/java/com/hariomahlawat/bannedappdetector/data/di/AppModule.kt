package com.hariomahlawat.bannedappdetector.data.di


import android.content.Context
import android.content.pm.PackageManager
import com.hariomahlawat.bannedappdetector.monitored.MonitoredAppsRepositoryImpl
import com.hariomahlawat.bannedappdetector.repository.MonitoredAppsRepository
import com.hariomahlawat.bannedappdetector.repository.ScanResultsRepository
import com.hariomahlawat.bannedappdetector.repository.ThemeRepository
import com.hariomahlawat.bannedappdetector.store.ScanResultsRepositoryImpl
import com.hariomahlawat.bannedappdetector.store.ThemeRepositoryImpl
import com.hariomahlawat.bannedappdetector.usecase.ComputeSummaryStatsUseCase
import com.hariomahlawat.bannedappdetector.usecase.GetScanResultsFlowUseCase
import com.hariomahlawat.bannedappdetector.usecase.ScanMonitoredAppsUseCase
import com.hariomahlawat.bannedappdetector.usecase.CheckForAppUpdateUseCase
import com.hariomahlawat.bannedappdetector.util.DispatcherProvider
import com.hariomahlawat.bannedappdetector.util.StandardDispatcherProvider
import com.hariomahlawat.bannedappdetector.update.AppUpdateRepository
import com.hariomahlawat.bannedappdetector.update.PlayAppUpdateRepository
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun dispatcherProvider(): DispatcherProvider = StandardDispatcherProvider()

    @Provides
    fun provideIoDispatcher(dp: DispatcherProvider): CoroutineDispatcher = dp.io

    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    fun provideClock(): () -> Long = { System.currentTimeMillis() }

    @Provides @Singleton
    fun monitoredRepo(impl: MonitoredAppsRepositoryImpl): MonitoredAppsRepository = impl

    @Provides @Singleton
    fun scanResultsRepo(impl: ScanResultsRepositoryImpl): ScanResultsRepository = impl

    @Provides @Singleton
    fun themeRepo(impl: ThemeRepositoryImpl): ThemeRepository = impl

    @Provides
    fun appUpdateManager(@ApplicationContext context: Context): AppUpdateManager =
        AppUpdateManagerFactory.create(context)

    @Provides @Singleton
    fun appUpdateRepo(impl: PlayAppUpdateRepository): AppUpdateRepository = impl

    @Provides
    fun scanUseCase(
        monitored: MonitoredAppsRepository,
        stored: ScanResultsRepository,
        pm: PackageManager,
        io: CoroutineDispatcher,
        clock: () -> Long
    ) = ScanMonitoredAppsUseCase(monitored, stored, pm, io, clock)

    @Provides
    fun resultsFlowUseCase(repo: ScanResultsRepository) = GetScanResultsFlowUseCase(repo)

    @Provides
    fun checkUpdateUseCase(repo: AppUpdateRepository) = CheckForAppUpdateUseCase(repo)

    @Provides
    fun summaryUseCase() = ComputeSummaryStatsUseCase()
}
