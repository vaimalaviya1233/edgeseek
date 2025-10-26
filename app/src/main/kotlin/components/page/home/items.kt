package net.lsafer.edgeseek.app.components.page.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import net.lsafer.edgeseek.app.*
import net.lsafer.edgeseek.app.components.lib.SingleSelectPreferenceListItem
import net.lsafer.edgeseek.app.components.lib.SwitchPreferenceListItem
import net.lsafer.edgeseek.app.l10n.strings

@Composable
context(local: Local)
fun HomePage_ListItem_activation(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    SwitchPreferenceListItem(
        value = local.repo.activated,
        onChange = { newValue ->
            local.repo.activated = newValue

            if (newValue) {
                coroutineScope.launch {
                    local.eventBus.startService.send(Unit)
                }
            }
        },
        headline = strings.stmt.app_activation_headline,
        supporting = strings.stmt.app_activation_supporting,
        modifier = modifier,
    )
}

@Composable
context(local: Local)
fun HomePage_ListItem_ui_colors(modifier: Modifier = Modifier) {
    SingleSelectPreferenceListItem(
        value = local.repo.uiColors,
        onChange = { local.repo.uiColors = it },
        headline = strings.stmt.app_colors_headline,
        items = mapOf(
            null to strings.stmt.app_colors_value_system,
            UI_COLORS_BLACK to strings.stmt.app_colors_value_black,
            UI_COLORS_DARK to strings.stmt.app_colors_value_dark,
            UI_COLORS_LIGHT to strings.stmt.app_colors_value_light,
            UI_COLORS_WHITE to strings.stmt.app_colors_value_white,
        ),
        modifier = modifier,
    )
}

@Composable
context(local: Local)
fun HomePage_ListItem_auto_boot(modifier: Modifier = Modifier) {
    SwitchPreferenceListItem(
        value = local.repo.autoBoot,
        onChange = { local.repo.autoBoot = it },
        headline = strings.stmt.app_auto_boot_headline,
        supporting = strings.stmt.app_auto_boot_supporting,
        modifier = modifier,
    )
}

@Composable
context(local: Local)
fun HomePage_ListItem_brightness_reset(modifier: Modifier = Modifier) {
    SwitchPreferenceListItem(
        value = local.repo.brightnessReset,
        onChange = { local.repo.brightnessReset = it },
        headline = strings.stmt.app_brightness_reset_headline,
        supporting = strings.stmt.app_brightness_reset_supporting,
        modifier = modifier,
    )
}
