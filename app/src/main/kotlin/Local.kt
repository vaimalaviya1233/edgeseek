package net.lsafer.edgeseek.app

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.TimeZone
import net.lsafer.edgeseek.app.support.CustomDimmerFacade
import net.lsafer.edgeseek.app.support.CustomToastFacade
import net.lsafer.edgeseek.app.support.Repo
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

    val repo = Repo()
}
