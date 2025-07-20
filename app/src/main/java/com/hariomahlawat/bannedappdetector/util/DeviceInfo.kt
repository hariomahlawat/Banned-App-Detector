package com.hariomahlawat.bannedappdetector.util

import android.content.Context
import android.os.Build
import android.provider.Settings

/** Information about the current device */
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val osVersion: String,
    val androidId: String
)

/**
 * Collect a minimal set of device information without requiring
 * additional runtime permissions.
 */
fun getDeviceInfo(context: Context): DeviceInfo {
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    return DeviceInfo(
        manufacturer = Build.MANUFACTURER,
        model = Build.MODEL,
        osVersion = Build.VERSION.RELEASE ?: Build.VERSION.SDK_INT.toString(),
        androidId = androidId
    )
}
