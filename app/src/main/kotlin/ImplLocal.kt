package net.lsafer.edgeseek.app

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import net.lsafer.edgeseek.app.impl.CustomDimmerFacade
import net.lsafer.edgeseek.app.impl.CustomToastFacade

class ImplLocal {
    lateinit var context: Context

    lateinit var defaultScope: CoroutineScope

    lateinit var toast: CustomToastFacade
    lateinit var dimmer: CustomDimmerFacade
}
