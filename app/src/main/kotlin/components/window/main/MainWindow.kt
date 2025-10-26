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
package net.lsafer.edgeseek.app.components.window.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.lsafer.compose.simplenav.SimpleNavHost
import net.lsafer.edgeseek.app.AppNavController
import net.lsafer.edgeseek.app.AppRoute
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.components.page.about.AboutPage
import net.lsafer.edgeseek.app.components.page.edge_edit.EdgeEditPage
import net.lsafer.edgeseek.app.components.page.edge_list.EdgeListPage
import net.lsafer.edgeseek.app.components.page.home.HomePage
import net.lsafer.edgeseek.app.components.page.log.LogPage
import net.lsafer.edgeseek.app.components.page.permissions.PermissionsPage
import net.lsafer.edgeseek.app.components.page.presets.PresetsPage
import net.lsafer.edgeseek.app.components.wizard.introduction.IntroductionWizard

@Composable
context(local: Local, navCtrl: AppNavController)
fun MainWindow(modifier: Modifier = Modifier) {
    SimpleNavHost(navCtrl) {
        entry<AppRoute.HomePage> { HomePage(modifier) }
        entry<AppRoute.EdgeListPage> { EdgeListPage(modifier) }
        entry<AppRoute.EdgeEditPage> { EdgeEditPage(it.pos, modifier) }
        entry<AppRoute.PermissionsPage> { PermissionsPage(modifier) }
        entry<AppRoute.PresetsPage> { PresetsPage(modifier) }
        entry<AppRoute.AboutPage> { AboutPage(modifier) }
        entry<AppRoute.LogPage> { LogPage(modifier) }
        entry<AppRoute.IntroductionWizard> { route ->
            IntroductionWizard(route, modifier)
        }
    }
}
