package net.lsafer.edgeseek.app.impl

import android.content.Context
import android.util.Log
import androidx.annotation.UiThread
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.data.settings.ActionFeature

sealed class ActionFeatureImpl {
    companion object {
        private val TAG = ActionFeatureImpl::class.simpleName!!

        fun from(feature: ActionFeature): ActionFeatureImpl? {
            return when (feature) {
                ActionFeature.Nothing -> null
                ActionFeature.ExpandStatusBar -> ExpandStatusBar
            }
        }
    }

    @UiThread
    context(_: Context, _: Local)
    abstract fun execute()

    data object ExpandStatusBar : ActionFeatureImpl() {
        context(ctx: Context, _: Local)
        override fun execute() {
            try {
                //noinspection JavaReflectionMemberAccess, WrongConstant
                Class.forName("android.app.StatusBarManager")
                    .getMethod("expandNotificationsPanel")
                    .invoke(ctx.getSystemService("statusbar"))
            } catch (e: ReflectiveOperationException) {
                Log.e(TAG, "Couldn't expand status bar", e)
            }
        }
    }
}
