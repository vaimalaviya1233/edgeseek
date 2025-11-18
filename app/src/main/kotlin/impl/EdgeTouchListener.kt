package net.lsafer.edgeseek.app.impl

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.data.settings.EdgeData
import net.lsafer.edgeseek.app.data.settings.EdgeSide
import kotlin.math.abs

class EdgeTouchListener(
    private val ctx: Context,
    private val local: Local,
    private val data: EdgeData,
    private val targetSide: EdgeSide,
    private val dpi: Int,

    private val onLongClick: ActionFeatureImpl?,
    private val onDoubleClick: ActionFeatureImpl?,

    private val onSeekImpl: ControlFeatureImpl?,
    private val onSwipeUp: ActionFeatureImpl?,
    private val onSwipeDown: ActionFeatureImpl?,
    private val onSwipeLeft: ActionFeatureImpl?,
    private val onSwipeRight: ActionFeatureImpl?,
) : View.OnTouchListener, GestureDetector.SimpleOnGestureListener() {
    private val detector = GestureDetector(ctx, this)

    private val xSeekSensitivityFactor = 155f * dpi
    private val xSwipeThresholdDistant = 10f * dpi

    private val xSwipeEnabled =
        onSwipeUp != null ||
                onSwipeDown != null ||
                onSwipeLeft != null ||
                onSwipeRight != null

    private var mCurrentOriginXOrY: Float? = null
    private var mCurrentOriginYOrX: Float? = null

    private var mCurrentSeekRange: IntRange? = null
    private var mCurrentSeekOrigin: Int? = null

    private var mIsScrolling: Boolean = false
    private var mIsDone: Boolean = false

    init {
        detector.setIsLongpressEnabled(onLongClick != null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).also {
            if (event.action == MotionEvent.ACTION_UP) {
                if (!mIsDone) doFeedbackVibration()
            }
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        mIsScrolling = false
        mIsDone = false
        mCurrentSeekRange = null
        mCurrentSeekOrigin = null
        mCurrentOriginXOrY = when (targetSide) {
            EdgeSide.Left, EdgeSide.Right -> e.y
            EdgeSide.Top, EdgeSide.Bottom -> e.x
        }
        mCurrentOriginYOrX = when (targetSide) {
            EdgeSide.Left, EdgeSide.Right -> e.x
            EdgeSide.Top, EdgeSide.Bottom -> e.y
        }

        if (onSeekImpl != null && !xSwipeEnabled) {
            mIsScrolling = true
            doFeedbackVibration()
            doFeedbackToast()
        }

        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean = context(ctx, local) {
        if (onDoubleClick != null) {
            mIsDone = true
            doFeedbackVibration()
            onDoubleClick.execute()
            return true
        }

        return false
    }

    override fun onLongPress(e: MotionEvent) = context(ctx, local) {
        if (onLongClick != null) {
            mIsDone = true
            doFeedbackVibration()
            onLongClick.execute()
        }
    }

    override fun onShowPress(e: MotionEvent) {
        if (onSeekImpl != null && xSwipeEnabled) {
            mIsScrolling = true
            doFeedbackVibration()
            doFeedbackToast()
        }
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean = context(ctx, local) {
        e1 ?: return false
        onSeekImpl ?: return false
        if (mIsDone) return false

        if (!mIsScrolling) {
            val now = SystemClock.uptimeMillis()

            if (now - e2.downTime > 300L) {
                mIsScrolling = true
                doFeedbackVibration()
                doFeedbackToast()
            } else {
                return false
            }
        }

        var deltaXOrY = when (targetSide) {
            EdgeSide.Left, EdgeSide.Right -> e1.y - e2.y
            EdgeSide.Top, EdgeSide.Bottom -> e1.x - e2.x
        }

        if (data.pos.seekReverse)
            deltaXOrY *= -1

        if (mCurrentSeekOrigin == null) {
            mCurrentSeekOrigin = onSeekImpl.fetchValue()
        }

        if (mCurrentSeekRange == null) {
            mCurrentSeekRange = if (data.pos.seekSteps)
                onSeekImpl.fetchStepRange(deltaXOrY.toInt())
            else
                onSeekImpl.fetchRange()
        }

        val factor = xSeekSensitivityFactor / (mCurrentSeekRange!!.last - mCurrentSeekRange!!.first)
        val accBoost = if (data.pos.seekAcceleration) abs(deltaXOrY / factor) else 1f
        val newValue = mCurrentSeekOrigin!! + ((deltaXOrY * accBoost) / factor * data.pos.sensitivity).toInt()
        val newValueCoerced = newValue.coerceIn(mCurrentSeekRange!!)

        val value = onSeekImpl.updateValue(newValueCoerced, data.pos.feedbackSystemPanel)

        if (data.pos.feedbackToast) {
            local.toast.update("$value")
        }

        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean = context(ctx, local) {
        val isVLeaning = abs(velocityX) < abs(velocityY)
        val isVFling = abs(velocityY) > xSwipeThresholdDistant
        val isHFling = abs(velocityX) > xSwipeThresholdDistant

        val isSkipUp = !isVFling || onSwipeUp == null || velocityY > 0
        val isSkipDown = !isVFling || onSwipeDown == null || velocityY < 0
        val isSkipLeft = !isHFling || onSwipeLeft == null || velocityX > 0
        val isSkipRight = !isHFling || onSwipeRight == null || velocityX < 0

        if (!isSkipUp && (isVLeaning || isSkipLeft && isSkipRight)) {
            mIsDone = true
            doFeedbackVibration()
            onSwipeUp.execute()
            return true
        }
        if (!isSkipDown && (isVLeaning || isSkipLeft && isSkipRight)) {
            mIsDone = true
            doFeedbackVibration()
            onSwipeDown.execute()
            return true
        }
        if (!isSkipLeft /* && (!isVerticalLeaning || isSkipUp && isSkipDown) */) {
            mIsDone = true
            doFeedbackVibration()
            onSwipeLeft.execute()
            return true
        }
        if (!isSkipRight /* && (!isVerticalLeaning || isSkipUp && isSkipDown) */) {
            mIsDone = true
            doFeedbackVibration()
            onSwipeRight.execute()
            return true
        }

        return false
    }

    private fun doFeedbackVibration() {
        if (data.pos.feedbackVibration > 0) {
            val vibrator = ctx.getSystemService(Vibrator::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        data.pos.feedbackVibration.toLong(),
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            else
                @Suppress("DEPRECATION")
                vibrator.vibrate(data.pos.feedbackVibration.toLong())
        }
    }

    private fun doFeedbackToast() = context(ctx, local) {
        if (onSeekImpl != null && data.pos.feedbackToast) {
            val value = onSeekImpl.fetchValue()
            local.toast.update("$value")
        }
    }
}
