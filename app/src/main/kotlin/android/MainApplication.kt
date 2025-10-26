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
package net.lsafer.edgeseek.app

import android.app.Application
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.lsafer.compose.simplenav.InMemorySimpleNavController
import net.lsafer.edgeseek.app.data.options.AndroidVars
import okio.Path.Companion.toOkioPath

class MainApplication : Application() {
    companion object {
        lateinit var globalLocal: Local
        lateinit var globalNavCtrl: UniNavController
    }

    override fun onCreate() {
        super.onCreate()

        runBlocking {
            val vars = AndroidVars(
                dataDir = filesDir.toOkioPath(),
                cacheDir = cacheDir.toOkioPath(),
            )

            val local = createAndroidLocal(vars)
            val navCtrl = InMemorySimpleNavController<UniRoute>(
                entries = when {
                    !local.repo.introduced ->
                        listOf(UniRoute.IntroductionWizard())

                    else ->
                        listOf(UniRoute.HomePage)
                },
            )

            globalLocal = local
            globalNavCtrl = navCtrl
        }

        globalLocal.ioScope.launch {
            for (it in globalLocal.eventBus.startService) {
                val context = this@MainApplication

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForegroundService(Intent(context, MainService::class.java))
                else
                    startService(Intent(context, MainService::class.java))
            }
        }
    }
}
