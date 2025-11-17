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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.snapshotFlow
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.android.MainApplication.Companion.globalLocal
import net.lsafer.edgeseek.app.data.settings.EdgePos
import net.lsafer.edgeseek.app.impl.EdgeViewFacade

/**
 * Persistent foreground service that manages edge overlays and background behavior.
 *
 * Responsibilities:
 * - Starts itself in the foreground with a notification (for Android O+).
 * - Observes app activation state and stops itself if deactivated.
 * - Listens to configuration changes to update edge overlays.
 * - Initializes and maintains `EdgeViewFacade` instances for all positions.
 * - Registers a screen-off broadcast receiver to handle screen-off events.
 *
 * All overlay updates and repository reads are reactive via Compose `snapshotFlow`,
 * ensuring UI-consistent state and automatic cleanup when flows complete.
 */
class MainService : Service() {
    companion object {
        /**
         * Starts the service appropriately depending on the Android version.
         */
        fun start(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ctx.startForegroundService(Intent(ctx, MainService::class.java))
            else
                ctx.startService(Intent(ctx, MainService::class.java))
        }
    }

    private val local = globalLocal
    private val coroutineScope = CoroutineScope(
        CoroutineName("MainServiceScope") + Dispatchers.Default + SupervisorJob() +
                CoroutineExceptionHandler { _, e ->
                    Log.e("MainServiceScope", "Unhandled coroutine exception", e)
                }
    )

    /** Counts configuration modifications; used to trigger overlay updates. */
    private val configModCount = MutableStateFlow(0)

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onCreate() {
        super.onCreate()
        startForeground()

        if (!local.repo.activated) {
            stopSelf()
            return
        }

        initScreenOffReceiver()
        initEdgeViewFacadesJob()
        initSelfStopJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configModCount.value++
    }

    /**
     * Launches a coroutine that observes configuration changes and maintains
     * edge overlay views for each [EdgePos].
     *
     * Each facade is updated reactively via a snapshot flow from the repository.
     * The facade is detached when the flow completes.
     */
    @Suppress("DEPRECATION")
    private fun initEdgeViewFacadesJob() {
        coroutineScope.launch {
            configModCount.collectLatest {
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

                coroutineScope {
                    for (pos in EdgePos.entries) launch {
                        val facade = EdgeViewFacade(
                            ctx = this@MainService,
                            local = local,
                            displayRotation = displayRotation,
                            displayHeight = displayHeight,
                            displayWidth = displayWidth,
                            displayDensityDpi = displayDensityDpi,
                        )

                        snapshotFlow { local.repo.getEdgeData(pos) }
                            .onEach { facade.update(it) }
                            .onCompletion { facade.detach() }
                            .flowOn(Dispatchers.Main)
                            .collect()
                    }
                }
            }
        }
    }

    /**
     * Observes the activation state in the repository.
     * Stops the service automatically if deactivated.
     */
    private fun initSelfStopJob() {
        snapshotFlow { local.repo.activated }
            .onEach { if (!it) stopSelf() }
            .launchIn(coroutineScope)
    }

    /**
     * Registers a broadcast receiver for ACTION_SCREEN_OFF events.
     * The receiver is automatically unregistered when the service coroutine scope is canceled.
     */
    private fun initScreenOffReceiver() {
        val receiver = ScreenOffBroadCastReceiver()
        registerReceiver(receiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
        coroutineScope.coroutineContext.job.invokeOnCompletion {
            unregisterReceiver(receiver)
        }
    }

    /**
     * Starts the service in the foreground with a minimal notification (O+ only).
     * Creates a notification channel if necessary.
     */
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
