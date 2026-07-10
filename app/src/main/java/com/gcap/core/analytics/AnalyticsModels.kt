package com.gcap.core.analytics

data class AnalyticsEventPayload(
    val event: String,
    val calculatorId: String? = null,
    val sessionId: String,
    val deviceId: String,
    val appVersion: String? = null,
    val platform: String = "android",
    val deviceManufacturer: String? = null,
    val deviceModel: String? = null,
    val deviceBrand: String? = null,
    val osVersion: String? = null,
    val durationMs: Long? = null,
    val success: Boolean? = null,
)

data class AnalyticsIngestPayload(
    val events: List<AnalyticsEventPayload>,
)

data class AnalyticsIngestResponse(
    val accepted: Int,
    val message: String,
)
