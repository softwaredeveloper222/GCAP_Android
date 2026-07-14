package com.gcap.core.notifications

import android.util.Log
import androidx.annotation.Keep
import com.onesignal.notifications.INotificationReceivedEvent
import com.onesignal.notifications.INotificationServiceExtension

/**
 * Runs when a push is received (including background / killed), so we can show the
 * home-list "New" badge before the user opens the app UI.
 */
@Keep
class SafetyDaysNotificationServiceExtension : INotificationServiceExtension {
    override fun onNotificationReceived(event: INotificationReceivedEvent) {
        val data = event.notification.additionalData
        if (!SafetyDaysNotificationStore.isSafetyDaysPush(data)) return

        val contentId = SafetyDaysNotificationStore.contentIdFromPushData(data)
        Log.i(TAG, "Background/extension push received contentId=$contentId")
        SafetyDaysNotificationStore.markPushArrived(event.context, contentId)
    }

    companion object {
        private const val TAG = "GcapOneSignalNSE"
    }
}
