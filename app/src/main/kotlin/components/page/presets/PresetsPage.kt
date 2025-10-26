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
package net.lsafer.edgeseek.app.components.page.presets

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.lsafer.edgeseek.app.*
import net.lsafer.edgeseek.app.components.lib.ListHeader
import net.lsafer.edgeseek.app.components.lib.ListSectionTitle
import net.lsafer.edgeseek.app.R

@Composable
context(local: Local)
fun PresetsPage(
    route: UniRoute.PresetsPage,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier),
        snackbarHost = {
            SnackbarHost(local.snackbar)
        },
    ) { innerPadding ->
        PresetsPageContent(Modifier.padding(innerPadding))
    }
}

@Composable
context(local: Local)
fun PresetsPageContent(modifier: Modifier = Modifier) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier),
    ) {
        ListHeader(
            title = stringResource(R.string.page_presets_heading),
            summary = stringResource(R.string.page_presets_summary),
        )
        ListSectionTitle(title = stringResource(R.string.presets))
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = PRESET_POS_STANDARD
                local.repo.edgeSideList = PRESET_SIDE_STANDARD
            },
            headlineContent = { Text(stringResource(R.string.preset_standard_headline)) },
            supportingContent = { Text(stringResource(R.string.preset_standard_supporting)) }
        )
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = PRESET_POS_STANDARD
                local.repo.edgeSideList = PRESET_SIDE_CENTERED
            },
            headlineContent = { Text(stringResource(R.string.preset_standard_c_headline)) },
            supportingContent = { Text(stringResource(R.string.preset_standard_c_supporting)) }
        )
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = PRESET_POS_BRIGHTNESS_ONLY
                local.repo.edgeSideList = PRESET_SIDE_STANDARD
            },
            headlineContent = { Text(stringResource(R.string.preset_brightness_headline)) },
            supportingContent = { Text(stringResource(R.string.preset_brightness_supporting)) }
        )
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = PRESET_POS_BRIGHTNESS_ONLY
                local.repo.edgeSideList = PRESET_SIDE_CENTERED
            },
            headlineContent = { Text(stringResource(R.string.preset_brightness_c_headline)) },
            supportingContent = { Text(stringResource(R.string.preset_brightness_c_supporting)) }
        )
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = PRESET_POS_DOUBLE_BRIGHTNESS
                local.repo.edgeSideList = PRESET_SIDE_STANDARD
            },
            headlineContent = { Text(stringResource(R.string.preset_brightness_d_headline)) },
            supportingContent = { Text(stringResource(R.string.preset_brightness_d_supporting)) }
        )
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = PRESET_POS_DOUBLE_BRIGHTNESS
                local.repo.edgeSideList = PRESET_SIDE_CENTERED
            },
            headlineContent = { Text(stringResource(R.string.preset_brightness_dc_headline)) },
            supportingContent = { Text(stringResource(R.string.preset_brightness_dc_supporting)) }
        )

        ListSectionTitle(title = stringResource(R.string.utility))
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = local.repo.edgePosList.map {
                    it.copy(color = Color(it.color).copy(alpha = 1f).toArgb())
                }
            },
            headlineContent = { Text(stringResource(R.string.show_all_headline)) },
            supportingContent = { Text(stringResource(R.string.show_all_supporting)) }
        )
        ListItem(
            modifier = Modifier.clickable {
                local.repo.edgePosList = local.repo.edgePosList.map {
                    it.copy(color = Color(it.color).copy(alpha = .01f).toArgb())
                }
            },
            headlineContent = { Text(stringResource(R.string.hide_all_headline)) },
            supportingContent = { Text(stringResource(R.string.hide_all_supporting)) }
        )

        Spacer(Modifier.height(50.dp))
    }
}
