package com.gcap.core.notifications

import android.content.Context
import android.util.Log
import com.gcap.BuildConfig
import com.gcap.core.analytics.AnalyticsApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SafetyDaysNotificationStore {

    private const val PREFS = "safety_days_notifications"
    private const val KEY_CACHE = "cached_payload"
    private const val KEY_SEEN_VERSION = "seen_version"
    private const val KEY_SEEN_ID = "seen_id"
    private const val TAG = "SafetyDaysNotify"

    private val gson = Gson()
    private var api: AnalyticsApiService? = null

    @Volatile
    private var cached: SafetyDaysPublicResponse? = null

    private fun ensureApi(): AnalyticsApiService {
        api?.let { return it }

        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.ANALYTICS_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.ANALYTICS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnalyticsApiService::class.java)
            .also { api = it }
    }

    fun getCached(context: Context): SafetyDaysPublicResponse? {
        cached?.let { return it }
        val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CACHE, null) ?: return null
        return try {
            gson.fromJson(json, SafetyDaysPublicResponse::class.java).also { cached = it }
        } catch (error: Exception) {
            Log.w(TAG, "Failed to parse cached Safety Days payload", error)
            null
        }
    }

    fun hasUnreadUpdate(context: Context): Boolean {
        val payload = getCached(context) ?: return false
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val seenId = prefs.getString(KEY_SEEN_ID, null)
        val seenVersion = prefs.getInt(KEY_SEEN_VERSION, 0)
        // Different content id counts as unread even if version numbers overlap.
        if (seenId != null && seenId != payload.id) return true
        return payload.version > seenVersion
    }

    fun markSeen(context: Context, id: String, version: Int) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SEEN_ID, id)
            .putInt(KEY_SEEN_VERSION, version)
            .apply()
    }

    /** @deprecated Prefer markSeen(context, id, version) */
    fun markSeen(context: Context, version: Int) {
        val id = getCached(context)?.id ?: return
        markSeen(context, id, version)
    }

    fun refresh(
        context: Context,
        contentId: String? = null,
        onResult: (SafetyDaysPublicResponse?, Boolean) -> Unit,
    ) {
        val appContext = context.applicationContext
        val id = contentId?.trim()?.takeIf { it.isNotEmpty() }
        ensureApi().getSafetyDaysNotification(id).enqueue(object : Callback<SafetyDaysPublicResponse> {
            override fun onResponse(
                call: Call<SafetyDaysPublicResponse>,
                response: Response<SafetyDaysPublicResponse>,
            ) {
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    Log.w(TAG, "Safety Days fetch failed: ${response.code()}")
                    onResult(getCached(appContext), false)
                    return
                }

                persist(appContext, body)
                onResult(body, true)
            }

            override fun onFailure(call: Call<SafetyDaysPublicResponse>, t: Throwable) {
                Log.w(TAG, "Safety Days network error", t)
                onResult(getCached(appContext), false)
            }
        })
    }

    private fun persist(context: Context, payload: SafetyDaysPublicResponse) {
        cached = payload
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CACHE, gson.toJson(payload))
            .apply()
    }
}
