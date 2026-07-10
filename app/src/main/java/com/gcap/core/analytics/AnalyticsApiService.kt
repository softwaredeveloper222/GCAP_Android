package com.gcap.core.analytics

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalyticsApiService {
    @POST("api/events")
    fun postEvents(@Body payload: AnalyticsIngestPayload): Call<AnalyticsIngestResponse>
}
