package net.lsafer.edgeseek.app

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.*
import kotlinx.datetime.TimeZone
import net.lsafer.edgeseek.app.data.options.AndroidVars
import net.lsafer.edgeseek.app.scripts.initAndroidLogFacade
import net.lsafer.edgeseek.app.scripts.initAndroidRepositories
import net.lsafer.edgeseek.app.scripts.registerAndroidShutdownHook
import kotlin.random.Random
import kotlin.time.Clock

context(app: Application)
suspend fun createAndroidLocal(vars: AndroidVars): Local {
    val local = Local()
    local.clock = Clock.System
    local.timeZone = TimeZone.currentSystemDefault()
    local.random = Random.Default
    local.ioScope = CoroutineScope(
        CoroutineName("LocalIoScope") + Dispatchers.IO + SupervisorJob() +
                CoroutineExceptionHandler { _, e ->
                    moduleLogger.e("Unhandled coroutine exception", e)
                }
    )
    local.snackbar = SnackbarHostState()
    context(local) {
        registerAndroidShutdownHook()
        initAndroidLogFacade()
        initAndroidRepositories(vars.dataDir)
    }
    return local
}
