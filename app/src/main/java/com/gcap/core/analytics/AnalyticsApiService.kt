package com.gcap.core.analytics

import com.gcap.core.notifications.SafetyDaysPublicResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AnalyticsApiService {
    @POST("api/events")
    fun postEvents(@Body payload: AnalyticsIngestPayload): Call<AnalyticsIngestResponse>

    @GET("api/notifications/safety-days/public")
    fun getSafetyDaysNotification(
        @Query("id") id: String? = null,
    ): Call<SafetyDaysPublicResponse>
}
