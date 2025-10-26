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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.runtime.snapshotFlow
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import net.lsafer.edgeseek.app.MainApplication.Companion.globalLocal
import net.lsafer.edgeseek.app.data.settings.EdgePos
import net.lsafer.edgeseek.app.data.settings.EdgePosData
import net.lsafer.edgeseek.app.data.settings.EdgeSide
import net.lsafer.edgeseek.app.data.settings.EdgeSideData
import net.lsafer.edgeseek.app.impl.CustomDimmerFacade
import net.lsafer.edgeseek.app.impl.CustomToastFacade
import net.lsafer.edgeseek.app.impl.launchEdgeViewJob
import net.lsafer.edgeseek.app.receiver.ScreenOffBroadCastReceiver

class MainService : Service() {
    private val local = globalLocal
    private val implLocal = ImplLocal()
    private var launchedEdgeViewJobsSubJobFlow = MutableSharedFlow<Job>(1)

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onCreate() {
        super.onCreate()
        implLocal.context = this
        implLocal.defaultScope = CoroutineScope(
            Dispatchers.Default + SupervisorJob() +
                    CoroutineExceptionHandler { _, e ->
                        moduleLogger.e("Unhandled coroutine exception", e)
                    }
        )
        implLocal.toast = CustomToastFacade(this)
        implLocal.dimmer = CustomDimmerFacade(this)
        startForeground()

        implLocal.defaultScope.launch {
            if (!local.repo.activated) {
                stopSelf()
                return@launch
            }

            launchReceiverJob()
            launchEdgeViewJobsSubJobsCleanupSubJob()
            launchEdgeViewJobsSubJob()
            launchSelfStopSubJob()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        implLocal.defaultScope.cancel()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        launchEdgeViewJobsSubJob()
    }

    @Suppress("DEPRECATION")
    private fun launchEdgeViewJobsSubJob() = context(implLocal) {
        val windowManager = getSystemService<WindowManager>()!!
        val display = windowManager.defaultDisplay
        val displayRotation = display.rotation
        val displayDensityDpi = resources.displayMetrics.densityDpi

        var displayHeight: Int
        var displayWidth: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            displayWidth = windowManager.currentWindowMetrics.bounds.width()
            displayWidth -= windowManager.currentWindowMetrics.windowInsets.systemWindowInsetLeft
            displayWidth -= windowManager.currentWindowMetrics.windowInsets.systemWindowInsetRight
            displayHeight = windowManager.currentWindowMetrics.bounds.height()
            displayHeight -= windowManager.currentWindowMetrics.windowInsets.systemWindowInsetTop
            displayHeight -= windowManager.currentWindowMetrics.windowInsets.systemWindowInsetBottom
        } else {
            val displayRealSize = Point()
            @Suppress("DEPRECATION")
            display.getRealSize(displayRealSize)
            displayWidth = displayRealSize.x
            displayHeight = displayRealSize.y
        }

        val subJob = Job(implLocal.defaultScope.coroutineContext.job)

        implLocal.defaultScope.launch {
            launchedEdgeViewJobsSubJobFlow.emit(subJob)

            launch(subJob) {
                for (side in EdgeSide.entries) {
                    val sideDataFlow = snapshotFlow {
                        local.repo.edgeSideList
                            .find { it.side.key == side.key }
                            ?: EdgeSideData(side)
                    }

                    for (pos in EdgePos.entries.filter { it.side == side }) {
                        val posDataFlow = snapshotFlow {
                            local.repo.edgePosList
                                .find { it.pos.key == pos.key }
                                ?: EdgePosData(pos)
                        }

                        launchEdgeViewJob(
                            windowManager = windowManager,
                            displayRotation = displayRotation,
                            displayHeight = displayHeight,
                            displayWidth = displayWidth,
                            displayDensityDpi = displayDensityDpi,
                            sideDataFlow = sideDataFlow,
                            posDataFlow = posDataFlow,
                        )
                    }
                }
            }
        }
    }

    private fun launchEdgeViewJobsSubJobsCleanupSubJob() {
        launchedEdgeViewJobsSubJobFlow
            .runningReduce { oldJob, newJob ->
                oldJob.cancel()
                newJob
            }
            .launchIn(implLocal.defaultScope)
    }

    private fun launchSelfStopSubJob() {
        snapshotFlow { local.repo.activated }
            .onEach { if (!it) stopSelf() }
            .launchIn(implLocal.defaultScope)
    }

    private fun launchReceiverJob() {
        val screenOffReceiver = ScreenOffBroadCastReceiver(implLocal)

        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

        implLocal.defaultScope.coroutineContext.job.invokeOnCompletion {
            unregisterReceiver(screenOffReceiver)
        }
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val title = getString(R.string.foreground_noti_title)
            val description = getString(R.string.foreground_noti_text)

            val channel = NotificationChannel("main", title, NotificationManager.IMPORTANCE_MIN)
            channel.description = description
            this.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, channel.id)
                .setContentTitle(getString(R.string.foreground_noti_title))
                .setContentText(getString(R.string.foreground_noti_text))
                .setSmallIcon(R.drawable.ic_sync)
                .build()
            this.startForeground(1, notification)
        }
    }
}
