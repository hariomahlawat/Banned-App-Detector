package com.hariomahlawat.bannedappdetector.permission

/** Interface for actions the user can take on a risky app. */
interface AppRiskActionHandler {
    fun openSettings(pkg: String)
    fun promptUninstall(pkg: String)
}
