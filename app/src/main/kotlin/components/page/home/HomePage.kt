/*
 *	Copyright 2020-2022 LSafer
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package net.lsafer.edgeseek.app.components.page.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.lsafer.edgeseek.app.*
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.android.MainService
import net.lsafer.edgeseek.app.components.lib.*

@Composable
context(local: Local, navCtrl: AppNavController)
fun HomePage(modifier: Modifier = Modifier) {
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier),
        snackbarHost = {
            SnackbarHost(local.snackbar)
        },
    ) { innerPadding ->
        MainPageContent(Modifier.padding(innerPadding))
    }
}

@Composable
context(local: Local, navCtrl: AppNavController)
fun MainPageContent(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier),
    ) {
        ListHeader(title = stringResource(R.string.app_name))

        ListSectionTitle(title = stringResource(R.string.application))
        SwitchPreferenceListItem(
            value = local.repo.activated,
            onChange = { newValue ->
                local.repo.activated = newValue
                if (newValue) MainService.start(ctx)
            },
            headline = stringResource(R.string.app_activation_headline),
            supporting = stringResource(R.string.app_activation_supporting),
            modifier = modifier,
        )
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

        ListDivider()
        ListSectionTitle(title = stringResource(R.string.job))
        ListItem(
            modifier = Modifier
                .clickable { navCtrl.push(AppRoute.EdgeListPage) },
            headlineContent = { Text(stringResource(R.string.page_edge_list_headline)) },
            supportingContent = { Text(stringResource(R.string.page_edge_list_supporting)) },
        )
        SwitchPreferenceListItem(
            value = local.repo.autoBoot,
            onChange = { local.repo.autoBoot = it },
            headline = stringResource(R.string.app_auto_boot_headline),
            supporting = stringResource(R.string.app_auto_boot_supporting),
            modifier = modifier,
        )
        SwitchPreferenceListItem(
            value = local.repo.brightnessReset,
            onChange = { local.repo.brightnessReset = it },
            headline = stringResource(R.string.app_brightness_reset_headline),
            supporting = stringResource(R.string.app_brightness_reset_supporting),
            modifier = modifier,
        )
        SwitchPreferenceListItem(
            value = local.repo.rotateEdges,
            onChange = { local.repo.rotateEdges = it },
            headline = stringResource(R.string.app_rotate_edges_headline),
            supporting = stringResource(R.string.app_rotate_edges_supporting),
            modifier = modifier,
        )

        ListDivider()
        ListSectionTitle(title = stringResource(R.string.misc))
        ListItem(
            modifier = Modifier
                .clickable { navCtrl.push(AppRoute.PermissionsPage) },
            headlineContent = { Text(stringResource(R.string.page_permissions_headline)) },
            supportingContent = { Text(stringResource(R.string.page_permissions_supporting)) },
        )
        ListItem(
            modifier = Modifier
                .clickable { navCtrl.push(AppRoute.PresetsPage) },
            headlineContent = { Text(stringResource(R.string.page_presets_headline)) },
            supportingContent = { Text(stringResource(R.string.page_presets_supporting)) },
        )
        ListItem(
            modifier = Modifier
                .clickable { navCtrl.push(AppRoute.AboutPage) },
            headlineContent = { Text(stringResource(R.string.page_about_headline)) },
            supportingContent = { Text(stringResource(R.string.page_about_supporting)) },
        )

        Spacer(Modifier.height(50.dp))
    }
}
