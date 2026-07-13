package com.gcap.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

@SuppressLint("QueryPermissionsNeeded")
fun openUrlInBrowser(context: Context, url: String) {
    val trimmed = url.trim()
    if (trimmed.isEmpty()) return

    try {
        val intent = Intent(Intent.ACTION_VIEW, trimmed.toUri()).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        // Don't gate on resolveActivity — package visibility on API 30+ often
        // returns null even when a browser is installed.
        context.startActivity(Intent.createChooser(intent, null))
    } catch (_: Exception) {
        android.widget.Toast
            .makeText(context, "Unable to open link", android.widget.Toast.LENGTH_SHORT)
            .show()
    }
}

val BASE_URL = "https://gcapcoolworks.com/"
val SAFETY_DAYS_URL = "https://safety-days.vercel.app/safety-days"