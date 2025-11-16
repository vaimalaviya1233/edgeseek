package net.lsafer.edgeseek.app

import net.lsafer.compose.simplenav.NavController
import net.lsafer.edgeseek.app.data.settings.EdgePos

typealias AppNavController = NavController<AppRoute>

sealed interface AppRoute {
    data object HomePage : AppRoute
    data object EdgeListPage : AppRoute
    data class EdgeEditPage(val pos: EdgePos) : AppRoute
    data object PermissionsPage : AppRoute
    data object PresetsPage : AppRoute
    data object AboutPage : AppRoute
    data object LogPage : AppRoute
    data object IntroWizard : AppRoute
}
