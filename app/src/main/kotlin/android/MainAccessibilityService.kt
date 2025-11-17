package net.lsafer.edgeseek.app.android

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainAccessibilityService : AccessibilityService() {
    companion object {
        var aliveState by mutableStateOf(false)
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        aliveState = true
    }

    override fun onDestroy() {
        super.onDestroy()
        aliveState = false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // See https://github.com/LSafer/edgeseek/tree/ebeb6df678a4a1ff02e9ea24dccef12d2e6d4086
    }

    override fun onInterrupt() {
    }
}
