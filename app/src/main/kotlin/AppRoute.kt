package net.lsafer.edgeseek.app

import net.lsafer.compose.simplenav.SimpleNavController
import net.lsafer.edgeseek.app.data.settings.EdgePos

typealias AppNavController = SimpleNavController<AppRoute>

sealed interface AppRoute {
    data object HomePage : AppRoute
    data object EdgeListPage : AppRoute
    data class EdgeEditPage(val pos: EdgePos) : AppRoute
    data object PermissionsPage : AppRoute
    data object PresetsPage : AppRoute
    data object AboutPage : AppRoute
    data object LogPage : AppRoute

    data class IntroductionWizard(val step: Step = Step.Welcome) : AppRoute {
        enum class Step {
            Welcome,
            Permissions,
            Presets,
            Done,
        }
    }
}
