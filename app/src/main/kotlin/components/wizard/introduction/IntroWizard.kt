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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import net.lsafer.compose.simplenav.NavHost
import net.lsafer.compose.simplenav.tangent
import net.lsafer.edgeseek.app.AppNavController
import net.lsafer.edgeseek.app.AppRoute
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.android.MainService
import net.lsafer.edgeseek.app.common.isMandatoryPermissionsMet
import net.lsafer.edgeseek.app.common.next
import net.lsafer.edgeseek.app.components.lib.WizardScaffold
import net.lsafer.edgeseek.app.components.page.permissions.PermissionsPageContent
import net.lsafer.edgeseek.app.components.page.presets.PresetsPageContent

private enum class IntroWizardStep {
    Welcome,
    Permissions,
    Presets,
    Done,
}

@Composable
context(local: Local, navCtrl: AppNavController)
fun IntroWizard(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val stepNavCtrl = remember {
        navCtrl.tangent(IntroWizardStep.Welcome)
    }

    NavHost(stepNavCtrl) {
        entry(IntroWizardStep.Welcome) {
            WizardScaffold(
                onCancel = { stepNavCtrl.back() },
                onComplete = { stepNavCtrl.push { it.next() } },
                modifier = modifier,
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(R.string.welcome_phrase))
                }
            }
        }
        entry(IntroWizardStep.Permissions) {
            WizardScaffold(
                onCancel = { stepNavCtrl.back() },
                onComplete = {
                    if (!ctx.isMandatoryPermissionsMet()) {
                        coroutineScope.launch {
                            local.snackbar.showSnackbar(
                                ctx.getString(R.string.mandatory_permissions_not_met)
                            )
                        }
                    } else {
                        local.repo.activated = true
                        MainService.start(ctx)
                        stepNavCtrl.push { it.next() }
                    }
                },
                modifier = modifier,
            ) {
                PermissionsPageContent()
            }
        }
        entry(IntroWizardStep.Presets) {
            WizardScaffold(
                onCancel = { stepNavCtrl.back() },
                onComplete = { stepNavCtrl.push { it.next() } },
                modifier = modifier,
            ) {
                PresetsPageContent()
            }
        }
        entry(IntroWizardStep.Done) {
            WizardScaffold(
                onCancel = { stepNavCtrl.back() },
                onComplete = {
                    local.repo.introduced = true
                    navCtrl.goToFirst()
                    navCtrl.replace(AppRoute.HomePage)
                },
                modifier = modifier,
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(R.string.all_setup_phrase))
                }
            }
        }
    }
}
