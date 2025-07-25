package com.hariomahlawat.bannedappdetector.permission

/**
 * Represents the risk level and type for an Android permission.
 */
data class PermissionInfo(
    val risk: String,
    val type: String
)

/** Simple wrapper for map loaded from JSON. */
data class PermissionMap(val map: Map<String, PermissionInfo>)
