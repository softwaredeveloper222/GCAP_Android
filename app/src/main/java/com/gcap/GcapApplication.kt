package com.gcap

import android.app.Application
import android.content.Intent
import android.util.Log
import com.gcap.main.safetyDays.SafetyDaysActivity
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import org.json.JSONObject

class GcapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val appId = BuildConfig.ONESIGNAL_APP_ID.trim()
        if (appId.isEmpty() || appId == "YOUR_ONESIGNAL_APP_ID") {
            Log.w(TAG, "OneSignal skipped: set ONESIGNAL_APP_ID in app/build.gradle.kts")
            return
        }

        // Must be set BEFORE init — OneSignal dashboard troubleshooting tip
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, appId)
        Log.i(TAG, "OneSignal initWithContext appId=$appId")

        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val data: JSONObject? = event.notification.additionalData
                val type = data?.optString("type").orEmpty()
                if (type != "safety_days" || data == null) return

                val contentId = data.optString("contentId")
                    .ifBlank { data.optString("id") }
                    .ifBlank { null }

                val intent = Intent(this@GcapApplication, SafetyDaysActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    if (contentId != null) {
                        putExtra(SafetyDaysActivity.EXTRA_CONTENT_ID, contentId)
                    }
                }
                startActivity(intent)
            }
        })

        // Do NOT request permission here — needs an Activity (see MainActivity after splash).
    }

    companion object {
        private const val TAG = "GcapOneSignal"
    }
}
