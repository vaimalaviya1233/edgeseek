package net.lsafer.edgeseek.app.impl

import co.touchlab.kermit.Logger
import net.lsafer.edgeseek.app.data.settings.ActionFeature

sealed class ActionFeatureImpl {
    companion object {
        private val logger = Logger.withTag(ActionFeatureImpl::class.qualifiedName!!)

        fun from(feature: ActionFeature): ActionFeatureImpl? {
            return when (feature) {
                ActionFeature.Nothing -> null
                ActionFeature.ExpandStatusBar -> ExpandStatusBar
            }
        }
    }

    context(implLocal: ImplLocal)
    abstract fun execute()

    data object ExpandStatusBar : ActionFeatureImpl() {
        context(implLocal: ImplLocal)
        override fun execute() {
            try {
                //noinspection JavaReflectionMemberAccess, WrongConstant
                Class.forName("android.app.StatusBarManager")
                    .getMethod("expandNotificationsPanel")
                    .invoke(implLocal.context.getSystemService("statusbar"))
            } catch (e: ReflectiveOperationException) {
                logger.e("Couldn't expand status bar", e)
            }
        }
    }
}
