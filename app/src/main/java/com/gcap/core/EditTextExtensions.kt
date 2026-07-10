package com.gcap.core

import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/** Runs [action] at most once per [intervalMs] — collapses duplicate IME / Enter / focus triggers. */
fun debounced(intervalMs: Long = 500, action: () -> Unit): () -> Unit {
    var lastRunMs = 0L
    return {
        val now = SystemClock.uptimeMillis()
        if (now - lastRunMs >= intervalMs) {
            lastRunMs = now
            action()
        }
    }
}

/** Prevents a view from receiving focus via keyboard navigation (avoids accidental Enter activation). */
fun View.disableKeyboardFocus() {
    isFocusable = false
    isFocusableInTouchMode = false
}

fun EditText.setOnEnterOrDone(action: () -> Unit) {
    setOnEditorActionListener { view, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val imm = context.getSystemService(InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            action()
            return@setOnEditorActionListener true
        }

        // Soft keyboard also sends a separate ENTER event — ignore it when IME is active.
        val imm = context.getSystemService(InputMethodManager::class.java)
        if (imm?.isActive(view) == true) {
            return@setOnEditorActionListener false
        }

        if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            action()
            return@setOnEditorActionListener true
        }

        false
    }
}
