package com.gcap.core.analytics

import android.app.Activity
import java.util.UUID

class CalculatorSessionTracker(
    private val activity: Activity,
    private val calculatorId: String,
) {
    private val sessionId: String = UUID.randomUUID().toString()
    private var startedAt: Long = 0L

    fun onStart() {
        CalculatorAnalytics.init(activity)
        startedAt = System.currentTimeMillis()
        CalculatorAnalytics.trackOpened(calculatorId, sessionId)
    }

    fun onStop() {
        if (startedAt <= 0L) return
        val durationMs = System.currentTimeMillis() - startedAt
        CalculatorAnalytics.trackSessionEnd(calculatorId, sessionId, durationMs)
        startedAt = 0L
    }

    fun trackCalculation(success: Boolean) {
        CalculatorAnalytics.init(activity)
        CalculatorAnalytics.trackCalculation(calculatorId, sessionId, success)
    }

    fun trackError() {
        CalculatorAnalytics.init(activity)
        CalculatorAnalytics.trackError(calculatorId, sessionId)
    }
}
