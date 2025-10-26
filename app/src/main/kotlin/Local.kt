package net.lsafer.edgeseek.app

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import net.lsafer.edgeseek.app.impl.CustomDimmerFacade
import net.lsafer.edgeseek.app.impl.CustomToastFacade
import net.lsafer.edgeseek.app.support.EventBus
import net.lsafer.edgeseek.app.support.MainRepository
import kotlin.random.Random
import kotlin.time.Clock

class Local {
    lateinit var clock: Clock
    lateinit var random: Random
    lateinit var timeZone: TimeZone
    lateinit var ioScope: CoroutineScope

    lateinit var snackbar: SnackbarHostState

    lateinit var toast: CustomToastFacade
    lateinit var dimmer: CustomDimmerFacade

    lateinit var fullLog: Flow<String>
    lateinit var repo: MainRepository

    val eventBus = EventBus()
}
