package com.gcap.core.analytics

import android.content.Context
import com.gcap.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

object CalculatorAnalytics {

    private const val PREFS_NAME = "calculator_analytics"
    private const val KEY_DEVICE_ID = "device_id"
    private const val KEY_PENDING_EVENTS = "pending_events"

    private val gson = Gson()
    private val isSending = AtomicBoolean(false)

    private lateinit var appContext: Context
    private lateinit var deviceId: String
    private lateinit var deviceInfo: DeviceInfo
    private lateinit var api: AnalyticsApiService

    fun init(context: Context) {
        if (::appContext.isInitialized) return

        appContext = context.applicationContext
        deviceId = getOrCreateDeviceId()
        deviceInfo = DeviceInfo.current()

        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.ANALYTICS_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        api = Retrofit.Builder()
            .baseUrl(BuildConfig.ANALYTICS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnalyticsApiService::class.java)

        flushPendingEvents()
    }

    fun trackOpened(calculatorId: String, sessionId: String) {
        send(
            baseEvent("calculator_opened", calculatorId, sessionId),
        )
    }

    fun trackCalculation(calculatorId: String, sessionId: String, success: Boolean) {
        send(
            baseEvent("calculator_calculation", calculatorId, sessionId, success = success),
        )
    }

    fun trackSessionEnd(
        calculatorId: String,
        sessionId: String,
        durationMs: Long,
    ) {
        send(
            baseEvent(
                "calculator_session_end",
                calculatorId,
                sessionId,
                durationMs = durationMs,
            ),
        )
    }

    fun trackError(calculatorId: String, sessionId: String) {
        send(
            baseEvent("calculator_error", calculatorId, sessionId, success = false),
        )
    }

  private fun baseEvent(
        event: String,
        calculatorId: String,
        sessionId: String,
        durationMs: Long? = null,
        success: Boolean? = null,
    ): AnalyticsEventPayload {
        return AnalyticsEventPayload(
            event = event,
            calculatorId = calculatorId,
            sessionId = sessionId,
            deviceId = deviceId,
            appVersion = BuildConfig.VERSION_NAME,
            deviceManufacturer = deviceInfo.manufacturer,
            deviceModel = deviceInfo.model,
            deviceBrand = deviceInfo.brand,
            osVersion = deviceInfo.osVersion,
            durationMs = durationMs,
            success = success,
        )
    }

    private fun send(event: AnalyticsEventPayload) {
        if (!BuildConfig.ANALYTICS_ENABLED) return
        ensureInitialized()
        enqueuePending(event)
        flushPendingEvents()
    }

    private fun enqueuePending(event: AnalyticsEventPayload) {
        val pending = loadPendingEvents().toMutableList()
        pending.add(event)
        savePendingEvents(pending)
    }

    private fun flushPendingEvents() {
        if (!BuildConfig.ANALYTICS_ENABLED) return
        if (!isSending.compareAndSet(false, true)) return

        val pending = loadPendingEvents()
        if (pending.isEmpty()) {
            isSending.set(false)
            return
        }

        api.postEvents(AnalyticsIngestPayload(pending)).enqueue(object : Callback<AnalyticsIngestResponse> {
            override fun onResponse(
                call: Call<AnalyticsIngestResponse>,
                response: Response<AnalyticsIngestResponse>,
            ) {
                isSending.set(false)
                if (response.isSuccessful) {
                    val remaining = loadPendingEvents()
                    if (remaining.size >= pending.size) {
                        savePendingEvents(remaining.drop(pending.size))
                    } else {
                        savePendingEvents(emptyList())
                    }
                }
                flushPendingEvents()
            }

            override fun onFailure(call: Call<AnalyticsIngestResponse>, t: Throwable) {
                isSending.set(false)
                android.util.Log.w("CalculatorAnalytics", "Failed to send events", t)
            }
        })
    }

    private fun ensureInitialized() {
        check(::appContext.isInitialized) {
            "CalculatorAnalytics.init(context) must be called before tracking events"
        }
    }

    private fun getOrCreateDeviceId(): String {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEVICE_ID, null) ?: UUID.randomUUID().toString().also { id ->
            prefs.edit().putString(KEY_DEVICE_ID, id).apply()
        }
    }

    private fun loadPendingEvents(): List<AnalyticsEventPayload> {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PENDING_EVENTS, null) ?: return emptyList()
        val type = object : TypeToken<List<AnalyticsEventPayload>>() {}.type
        return runCatching { gson.fromJson<List<AnalyticsEventPayload>>(json, type) }
            .getOrDefault(emptyList())
    }

    private fun savePendingEvents(events: List<AnalyticsEventPayload>) {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_PENDING_EVENTS, gson.toJson(events))
            .apply()
    }
}
