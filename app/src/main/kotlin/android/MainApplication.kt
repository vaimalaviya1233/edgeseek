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
package net.lsafer.edgeseek.app.android

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.*
import kotlinx.datetime.TimeZone
import net.lsafer.compose.simplenav.InMemorySimpleNavController
import net.lsafer.edgeseek.app.AppNavController
import net.lsafer.edgeseek.app.AppRoute
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.scripts.initRepositories
import net.lsafer.edgeseek.app.scripts.registerShutdownHook
import net.lsafer.edgeseek.app.support.CustomDimmerFacade
import net.lsafer.edgeseek.app.support.CustomToastFacade
import kotlin.random.Random
import kotlin.time.Clock

class MainApplication : Application() {
    companion object {
        lateinit var globalLocal: Local
        lateinit var globalNavCtrl: AppNavController
    }

    override fun onCreate() {
        super.onCreate()

        val local = Local()
        local.clock = Clock.System
        local.timeZone = TimeZone.currentSystemDefault()
        local.random = Random.Default
        local.ioScope = CoroutineScope(
            CoroutineName("LocalIoScope") + Dispatchers.IO + SupervisorJob() +
                    CoroutineExceptionHandler { _, e ->
                        Log.e("LocalIoScope", "Unhandled coroutine exception", e)
                    }
        )
        local.snackbar = SnackbarHostState()
        local.toast = CustomToastFacade(this)
        local.dimmer = CustomDimmerFacade(this)

        context(local) {
            registerShutdownHook()
            initRepositories(filesDir.resolve("datastore.json"))
        }

        val navCtrl = InMemorySimpleNavController<AppRoute>(
            entries = when {
                !local.repo.introduced ->
                    listOf(AppRoute.IntroductionWizard())

                else ->
                    listOf(AppRoute.HomePage)
            },
        )

        globalLocal = local
        globalNavCtrl = navCtrl
    }
}
