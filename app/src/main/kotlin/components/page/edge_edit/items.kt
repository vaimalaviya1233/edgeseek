package net.lsafer.edgeseek.app.components.page.edge_edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.components.lib.SingleSelectPreferenceListItem
import net.lsafer.edgeseek.app.data.settings.ActionFeature
import net.lsafer.edgeseek.app.data.settings.ControlFeature

@Composable
fun ControlFeaturePreferenceListItem(
    value: ControlFeature,
    onChange: (ControlFeature) -> Unit,
    headline: String,
    modifier: Modifier = Modifier,
) {
    SingleSelectPreferenceListItem(
        value = value,
        onChange = onChange,
        headline = headline,
        items = mapOf(
            ControlFeature.Nothing to stringResource(R.string.control_feature_nothing),
            ControlFeature.Brightness to stringResource(R.string.control_feature_brightness),
            ControlFeature.BrightnessWithDimmer to stringResource(R.string.control_feature_brightness_dimmer),
            ControlFeature.Alarm to stringResource(R.string.control_feature_alarm),
            ControlFeature.Music to stringResource(R.string.control_feature_music),
            ControlFeature.Ring to stringResource(R.string.control_feature_ring),
            ControlFeature.System to stringResource(R.string.control_feature_system),
        ),
        modifier = modifier,
    )
}

@Composable
fun ActionFeaturePreferenceListItem(
    value: ActionFeature,
    onChange: (ActionFeature) -> Unit,
    headline: String,
    modifier: Modifier = Modifier,
) {
    SingleSelectPreferenceListItem(
        value = value,
        onChange = onChange,
        headline = headline,
        items = mapOf(
            ActionFeature.Nothing to stringResource(R.string.action_feature_nothing),
            ActionFeature.ExpandStatusBar to stringResource(R.string.action_feature_expand_status_bar),
        ),
        modifier = modifier,
    )
}
