package com.gcap.core.analytics

import android.os.Build

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val brand: String,
    val osVersion: String,
    val sdkInt: Int,
) {
    fun displayName(): String = listOf(manufacturer, model)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Unknown device" }

    companion object {
        fun current(): DeviceInfo = DeviceInfo(
            manufacturer = Build.MANUFACTURER.orEmpty(),
            model = Build.MODEL.orEmpty(),
            brand = Build.BRAND.orEmpty(),
            osVersion = Build.VERSION.RELEASE.orEmpty(),
            sdkInt = Build.VERSION.SDK_INT,
        )
    }
}
