package com.gcap

import android.app.Application
import android.content.Intent
import android.util.Log
import com.gcap.core.notifications.SafetyDaysNotificationStore
import com.gcap.main.safetyDays.SafetyDaysActivity
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.onesignal.notifications.INotificationLifecycleListener
import com.onesignal.notifications.INotificationWillDisplayEvent

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

        OneSignal.Notifications.addForegroundLifecycleListener(
            object : INotificationLifecycleListener {
                override fun onWillDisplay(event: INotificationWillDisplayEvent) {
                    val data = event.notification.additionalData
                    if (!SafetyDaysNotificationStore.isSafetyDaysPush(data)) return

                    val contentId = SafetyDaysNotificationStore.contentIdFromPushData(data)
                    Log.i(TAG, "Foreground push received contentId=$contentId")
                    SafetyDaysNotificationStore.markPushArrived(this@GcapApplication, contentId)
                    // Keep default display behavior (system banner while in app).
                }
            },
        )

        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val data = event.notification.additionalData
                if (!SafetyDaysNotificationStore.isSafetyDaysPush(data) || data == null) return

                val contentId = SafetyDaysNotificationStore.contentIdFromPushData(data)
                SafetyDaysNotificationStore.markPushArrived(this@GcapApplication, contentId)

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
