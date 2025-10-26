package net.lsafer.edgeseek.app.impl

import android.content.Context
import kotlinx.coroutines.CoroutineScope

class ImplLocal {
    lateinit var context: Context

    lateinit var defaultScope: CoroutineScope

    lateinit var toast: CustomToastFacade
    lateinit var dimmer: CustomDimmerFacade
}
