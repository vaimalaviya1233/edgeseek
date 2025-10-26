package net.lsafer.edgeseek.app.components.page.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import net.lsafer.edgeseek.app.*
import net.lsafer.edgeseek.app.components.lib.SingleSelectPreferenceListItem
import net.lsafer.edgeseek.app.components.lib.SwitchPreferenceListItem

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
        headline = stringResource(R.string.app_activation_headline),
        supporting = stringResource(R.string.app_activation_supporting),
        modifier = modifier,
    )
}

@Composable
context(local: Local)
fun HomePage_ListItem_ui_colors(modifier: Modifier = Modifier) {
    SingleSelectPreferenceListItem(
        value = local.repo.uiColors,
        onChange = { local.repo.uiColors = it },
        headline = stringResource(R.string.app_colors_headline),
        items = mapOf(
            null to stringResource(R.string.app_colors_value_system),
            UI_COLORS_BLACK to stringResource(R.string.app_colors_value_black),
            UI_COLORS_DARK to stringResource(R.string.app_colors_value_dark),
            UI_COLORS_LIGHT to stringResource(R.string.app_colors_value_light),
            UI_COLORS_WHITE to stringResource(R.string.app_colors_value_white),
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
        headline = stringResource(R.string.app_auto_boot_headline),
        supporting = stringResource(R.string.app_auto_boot_supporting),
        modifier = modifier,
    )
}

@Composable
context(local: Local)
fun HomePage_ListItem_brightness_reset(modifier: Modifier = Modifier) {
    SwitchPreferenceListItem(
        value = local.repo.brightnessReset,
        onChange = { local.repo.brightnessReset = it },
        headline = stringResource(R.string.app_brightness_reset_headline),
        supporting = stringResource(R.string.app_brightness_reset_supporting),
        modifier = modifier,
    )
}
