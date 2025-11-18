package net.lsafer.edgeseek.app.support

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.UiThread
import androidx.core.content.getSystemService

/**
 * Provides a fullscreen dimmer overlay using a system window.
 *
 * This facade manages adding, updating, and removing a transparent black view
 * on top of all content, allowing the app to control screen dimming intensity.
 */
class CustomDimmerFacade(context: Context) {
    companion object {
        private val TAG = CustomDimmerFacade::class.simpleName!!
    }

    private val windowManager = context.getSystemService<WindowManager>()!!
    private val windowParams = WindowManager.LayoutParams()

    /** The dimmer view that gets added/removed from the window. */
    private var view = View(context)

    /** Whether the dimmer view is currently attached to the window. */
    private var attached = false

    /** Current dimming value in the range 0–255. */
    var currentValue: Int = 0
        private set

    init {
        @Suppress("DEPRECATION")
        windowParams.type = when {
            Build.VERSION.SDK_INT >= 26 ->
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

            else ->
                WindowManager.LayoutParams.TYPE_PHONE
        }
        @Suppress("DEPRECATION")
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN or
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS

        val windowSize = Point()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getSize(windowSize)
        windowParams.height = windowSize.y * 3
        windowParams.width = windowSize.x * 3
    }

    /**
     * Updates the dimmer intensity.
     *
     * @param value The opacity level from 0 (off) to 255 (fully opaque black).
     * Attaches the dimmer if needed, updates it if already attached, or removes it when 0.
     */
    @UiThread
    fun update(value: Int) {
        require(value in 0..255) { "Bad alpha value: $value" }

        currentValue = value

        if (value == 0) {
            if (attached) {
                runCatching { windowManager.removeView(view) }
                    .onSuccess { attached = false }
            }
        } else {
            view.setBackgroundColor(Color.argb(255, 0, 0, 0))
            view.alpha = value / 255f
            windowParams.alpha = value / 255f

            if (!attached) {
                runCatching { windowManager.addView(view, windowParams) }
                    .onFailure { e -> Log.e(TAG, "failed to attach dimmer to window", e) }
                    .onSuccess { attached = true }
            } else {
                runCatching { windowManager.updateViewLayout(view, windowParams) }
                    .onFailure { e -> Log.e(TAG, "failed to update dimmer window params", e) }
            }
        }
    }
}
