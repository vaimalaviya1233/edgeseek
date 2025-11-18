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
package net.lsafer.edgeseek.app.components.page.about

import android.content.Intent
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
import androidx.core.net.toUri
import net.lsafer.edgeseek.app.*
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.components.lib.ListDivider
import net.lsafer.edgeseek.app.components.lib.ListHeader
import net.lsafer.edgeseek.app.components.lib.ListSectionTitle

@Composable
context(local: Local, navCtrl: AppNavController)
fun AboutPage(modifier: Modifier = Modifier) {
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier),
        snackbarHost = {
            SnackbarHost(local.snackbar)
        },
    ) { innerPadding ->
        AboutPageContent(Modifier.padding(innerPadding))
    }
}

@Composable
context(_: Local, navCtrl: AppNavController)
fun AboutPageContent(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current

    fun openIntroductionWizard() {
        navCtrl.goToFirst()
        navCtrl.replace(AppRoute.IntroWizard)
    }

    fun openUrl(urlString: String) {
        val uri = urlString.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        ctx.startActivity(intent)
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        ListHeader(title = stringResource(R.string.page_about_heading))
        ListSectionTitle(title = stringResource(R.string.credits))
        ListItem(
            headlineContent = { Text(stringResource(R.string.author)) },
            supportingContent = { Text("LSafer") }
        )

        ListDivider()
        ListSectionTitle(title = stringResource(R.string.version))
        ListItem(
            headlineContent = { Text(stringResource(R.string.version_name)) },
            supportingContent = { Text(BuildConfig.VERSION) },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.version_code)) },
            supportingContent = { Text(BuildConfig.VERSION_CODE.toString()) },
        )

        ListDivider()
        ListSectionTitle(title = stringResource(R.string.links))
        ListItem(
            modifier = Modifier
                .clickable { openUrl("https://lsafer.net/edgeseek") },
            headlineContent = { Text(stringResource(R.string.about_website_headline)) },
            supportingContent = { Text(stringResource(R.string.about_website_supporting)) },
        )
        ListItem(
            modifier = Modifier
                .clickable { openUrl("https://github.com/lsafer/edgeseek") },
            headlineContent = { Text(stringResource(R.string.about_source_code_headline)) },
            supportingContent = { Text(stringResource(R.string.about_source_code_supporting)) }
        )

        ListDivider()
        ListSectionTitle(title = stringResource(R.string.misc))
        ListItem(
            modifier = Modifier
                .clickable { openIntroductionWizard() },
            headlineContent = { Text(stringResource(R.string.about_reintroduce_headline)) },
            supportingContent = { Text(stringResource(R.string.about_reintroduce_supporting)) },
        )
        ListItem(
            modifier = Modifier
                .clickable { navCtrl.push(AppRoute.LogPage) },
            headlineContent = { Text(stringResource(R.string.page_log_headline)) },
            supportingContent = { Text(stringResource(R.string.page_log_supporting)) },
        )

        Spacer(Modifier.height(50.dp))
    }
}
