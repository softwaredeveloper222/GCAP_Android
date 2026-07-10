package com.gcap.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri

fun isPdfUrl(url: String): Boolean {
    val path = url.substringBefore('?').substringBefore('#')
    return path.endsWith(".pdf", ignoreCase = true)
}

fun openPdfOrExternal(context: Context, webView: WebView, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
        return
    }

    val viewerUrl = "https://docs.google.com/gview?embedded=true&url=${Uri.encode(url)}"
    webView.loadUrl(viewerUrl)
}

fun externalLinkWebViewClient(
    webView: WebView,
    onPageFinished: (() -> Unit)? = null,
): WebViewClient {
    val handleUrl: (String) -> Boolean = { url ->
        if (isPdfUrl(url)) {
            openPdfOrExternal(webView.context, webView, url)
            true
        } else {
            false
        }
    }

    return object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return handleUrl(request.url.toString())
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            onPageFinished?.invoke()
        }
    }
}

fun attachPopupWindowHandler(
    targetWebView: WebView,
    onUrl: (String) -> Boolean,
    resultMsg: Message?,
): Boolean {
    val popupWebView = WebView(targetWebView.context)
    popupWebView.settings.javaScriptEnabled = true
    popupWebView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            if (onUrl(url)) return true
            targetWebView.loadUrl(url)
            return true
        }
    }
    val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
    transport.webView = popupWebView
    resultMsg.sendToTarget()
    return true
}
