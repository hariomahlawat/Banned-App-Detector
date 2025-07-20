package com.hariomahlawat.bannedappdetector.usecase


import com.hariomahlawat.bannedappdetector.MonitoredAppMeta
import com.hariomahlawat.bannedappdetector.MonitoredStatus
import com.hariomahlawat.bannedappdetector.ScanResult
import com.hariomahlawat.core.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class ComputeSummaryStatsUseCaseTest {
    private val useCase = ComputeSummaryStatsUseCase()

    @Test
    fun stats_correct() {
        val now = 0L
        val list = listOf(
            ScanResult(MonitoredAppMeta("a","A"), MonitoredStatus.INSTALLED_ENABLED, scannedAt = now),
            ScanResult(MonitoredAppMeta("b","B"), MonitoredStatus.INSTALLED_DISABLED, scannedAt = now),
            ScanResult(MonitoredAppMeta("c","C"), MonitoredStatus.NOT_INSTALLED, scannedAt = now)
        )
        val s = useCase(list)
        assertEquals(3, s.totalMonitored)
        assertEquals(1, s.installedEnabled)
        assertEquals(1, s.installedDisabled)
        assertEquals(1, s.notInstalled)
    }
}
