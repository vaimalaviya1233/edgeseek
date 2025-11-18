package net.lsafer.edgeseek.app.impl

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.annotation.UiThread
import androidx.cardview.widget.CardView
import androidx.core.content.getSystemService
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.data.settings.EdgeCorner
import net.lsafer.edgeseek.app.data.settings.EdgeData
import net.lsafer.edgeseek.app.data.settings.EdgeSide
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Facade for displaying and interacting with a single-edge overlay view.
 *
 * The view updates its size, position, color, and interaction behavior via [update],
 * and can be removed from the window with [detach].
 */
class EdgeViewFacade(
    private val ctx: Context,
    private val local: Local,
    private val displayRotation: Int,
    private val displayHeight: Int,
    private val displayWidth: Int,
    private val displayDensityDpi: Int,
) {
    companion object {
        private val TAG = EdgeViewFacade::class.simpleName!!
    }

    private val windowManager = ctx.getSystemService<WindowManager>()!!

    /** The CardView representing the edge overlay. */
    private val view = CardView(ctx)
    /** Layout parameters describing size, position, alpha, and gravity of the view. */
    private val windowParams = LayoutParams()

    init {
        view.radius = 25f
        view.elevation = 1f
        windowParams.type = when {
            Build.VERSION.SDK_INT >= 26 ->
                LayoutParams.TYPE_APPLICATION_OVERLAY

            else ->
                LayoutParams.TYPE_PHONE
        }
        @Suppress("DEPRECATION")
        windowParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE or
                LayoutParams.FLAG_NOT_TOUCH_MODAL or
                LayoutParams.FLAG_SHOW_WHEN_LOCKED // <-- this doesn't work for some reason
    }

    /**
     * Updates the edge view according to the given [EdgeData].
     *
     * - Attaches or removes the view depending on activation, segment inclusion,
     *   and orientation filters.
     * - Sets size, position, alpha, and gravity based on edge configuration and display rotation.
     * - Applies background color and touch gestures (tap, long press, double tap, swipes).
     *
     * @param data The edge configuration and interaction definitions.
     */
    @UiThread
    fun update(data: EdgeData) {
        if (
            !data.pos.activated ||
            !data.pos.id.isIncludedWhenSegmented(data.side.nSegments) ||
            !data.pos.orientationFilter.test(displayRotation)
        ) {
            runCatching { windowManager.removeView(view) }
            return
        }

        var targetSide = data.pos.id.side
        var targetCorner = data.pos.id.corner

        // edges are rotated with display rotation by default, this rotates them back.
        if (!local.repo.rotateEdges) {
            targetSide = targetSide.rotate(displayRotation)
            targetCorner = targetCorner.rotate(displayRotation)
        }

        val lengthPct = 1f / data.side.nSegments
        val windowLength = when (targetSide) {
            EdgeSide.Left, EdgeSide.Right -> displayHeight
            EdgeSide.Top, EdgeSide.Bottom -> displayWidth
        }

        val length = (lengthPct * windowLength).roundToInt()

        windowParams.height = when (targetSide) {
            EdgeSide.Left, EdgeSide.Right -> length
            EdgeSide.Top, EdgeSide.Bottom -> data.pos.thickness
        }
        windowParams.width = when (targetSide) {
            EdgeSide.Left, EdgeSide.Right -> data.pos.thickness
            EdgeSide.Top, EdgeSide.Bottom -> length
        }
        windowParams.gravity = when (targetCorner) {
            EdgeCorner.BottomRight -> Gravity.BOTTOM or Gravity.RIGHT
            EdgeCorner.Bottom -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            EdgeCorner.BottomLeft -> Gravity.BOTTOM or Gravity.LEFT
            EdgeCorner.Left -> Gravity.LEFT or Gravity.CENTER_VERTICAL
            EdgeCorner.TopLeft -> Gravity.TOP or Gravity.LEFT
            EdgeCorner.Top -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            EdgeCorner.TopRight -> Gravity.TOP or Gravity.RIGHT
            EdgeCorner.Right -> Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
        windowParams.alpha = max(Color.alpha(data.pos.color) / 255f, 0.5f) // 0.5f = 128/255

        view.setCardBackgroundColor(data.pos.color)
        view.alpha = Color.alpha(data.pos.color) / 255f

        view.setOnTouchListener(
            EdgeTouchListener(
                ctx = ctx,
                local = local,
                edgePosData = data.pos,
                edgeSide = targetSide,
                dpi = displayDensityDpi,
                onSeekImpl = ControlFeatureImpl.from(data.pos.onSeek),
                onLongClick = ActionFeatureImpl.from(data.pos.onLongClick),
                onDoubleClick = ActionFeatureImpl.from(data.pos.onDoubleClick),
                onSwipeUp = ActionFeatureImpl.from(data.pos.onSwipeUp),
                onSwipeDown = ActionFeatureImpl.from(data.pos.onSwipeDown),
                onSwipeLeft = ActionFeatureImpl.from(data.pos.onSwipeLeft),
                onSwipeRight = ActionFeatureImpl.from(data.pos.onSwipeRight),
            )
        )

        runCatching { windowManager.removeView(view) }
        runCatching { windowManager.addView(view, windowParams) }
            .onFailure { e -> Log.e(TAG, "failed adding view to window", e) }
    }

    /**
     * Removes the edge view from the window.
     *
     * Safe to call multiple times. Ensures the overlay is detached and resources
     * associated with the view are cleaned up.
     */
    @UiThread
    fun detach() {
        runCatching { windowManager.removeView(view) }
    }
}
