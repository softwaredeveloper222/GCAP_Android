package com.gcap.core.api

import com.gcap.core.models.ValveItem
import com.gcap.core.models.ChartItem
import com.gcap.core.models.AnimationItem
import com.gcap.core.models.IndustryItem
import com.gcap.core.models.ContactInfoItem
import com.gcap.core.models.ContactUsResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("get_valve.php")
    fun getValves(): Call<List<ValveItem>>

    @GET("get_charts.php")
    fun getCharts(): Call<List<ChartItem>>

    @GET("get_animations.php")
    fun getAnimations(): Call<List<AnimationItem>>

    @GET("get_industry.php")
    fun getIndustry(): Call<List<IndustryItem>>

    @GET("get_contact_information.php")
    fun getContactInfo(): Call<ContactInfoItem>

    @FormUrlEncoded
    @POST("contactus.php")
    fun submitContactUs(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("message") message: String
    ): Call<ContactUsResponse>
}