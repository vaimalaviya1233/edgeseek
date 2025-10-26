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
package net.lsafer.edgeseek.app.components.wizard.introduction

import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.lsafer.edgeseek.app.*
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.android.MainService
import net.lsafer.edgeseek.app.components.page.presets.PresetsPageContent

@Composable
context(local: Local, navCtrl: AppNavController)
fun IntroductionWizard(
    route: AppRoute.IntroductionWizard,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val steps = AppRoute.IntroductionWizard.Step.entries

    val onStepCancel: () -> Unit = {
        navCtrl.back()
    }

    val onStepConfirm: () -> Unit = {
        val i = route.step.ordinal + 1

        if (i <= steps.size)
            navCtrl.push(route.copy(step = steps[i]))
    }

    val onPermissionsStepConfirm: () -> Unit = {
        if (
            !Settings.canDrawOverlays(ctx) ||
            !Settings.System.canWrite(ctx)
        ) {
            coroutineScope.launch {
                local.snackbar.showSnackbar(
                    ctx.getString(R.string.mandatory_permissions_not_met)
                )
            }
        } else {
            local.repo.activated = true
            MainService.start(ctx)
            onStepConfirm()
        }
    }

    val onComplete: () -> Unit = {
        local.repo.introduced = true
        while (navCtrl.back());
        navCtrl.push(AppRoute.HomePage)
    }

    when (route.step) {
        AppRoute.IntroductionWizard.Step.Welcome -> {
            IntroductionWizardWrapper(
                onConfirm = onStepConfirm,
                onCancel = onStepCancel,
                modifier = modifier,
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(R.string.welcome_phrase))
                }
            }
        }

        AppRoute.IntroductionWizard.Step.Permissions ->
            IntroductionWizardWrapper(
                onConfirm = onPermissionsStepConfirm,
                onCancel = onStepCancel,
                modifier = modifier,
                content = {
//                    PermissionsPageContent() // fixme
                }
            )

        AppRoute.IntroductionWizard.Step.Presets ->
            IntroductionWizardWrapper(
                onConfirm = onStepConfirm,
                onCancel = onStepCancel,
                modifier = modifier,
                content = { PresetsPageContent() }
            )

        AppRoute.IntroductionWizard.Step.Done -> {
            IntroductionWizardWrapper(
                onConfirm = onComplete,
                onCancel = onStepCancel,
                modifier = modifier,
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(R.string.all_setup_phrase))
                }
            }
        }
    }
}

@Composable
context(local: Local)
fun IntroductionWizardWrapper(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                content()
            }

            Surface(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(vertical = 5.dp, horizontal = 75.dp)) {
                    TextButton(onClick = { onCancel() }) {
                        Text(stringResource(R.string.back))
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    TextButton({ onConfirm() }) {
                        Text(stringResource(R.string.next), color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
