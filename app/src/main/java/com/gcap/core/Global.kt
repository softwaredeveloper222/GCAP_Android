package com.gcap.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

@SuppressLint("QueryPermissionsNeeded")
fun openUrlInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
//        android.widget.Toast.makeText(context, "No browser found", android.widget.Toast.LENGTH_SHORT).show()
    }
}

val BASE_URL = "https://gcapcoolworks.com/"