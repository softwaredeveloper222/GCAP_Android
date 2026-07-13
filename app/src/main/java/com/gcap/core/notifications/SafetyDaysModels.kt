package com.gcap.core.notifications

data class SafetyDaysPublicResponse(
    val id: String,
    val version: Int,
    val publishedAt: String? = null,
    val updatedAt: String? = null,
    val content: SafetyDaysContent,
)

data class SafetyDaysContent(
    val title: String,
    val subtitle: String? = null,
    val eventName: String? = null,
    val dateLabel: String? = null,
    val location: String? = null,
    val priceAttendee: String? = null,
    val priceExhibitor: String? = null,
    val bullets: List<String> = emptyList(),
    val registerUrl: String? = null,
    val hotelsUrl: String? = null,
    val bodyHtml: String? = null,
    val heroImageUrl: String? = null,
    val images: List<SafetyDaysImage> = emptyList(),
)

data class SafetyDaysImage(
    val url: String,
    val alt: String? = null,
)
