package net.lsafer.edgeseek.app.scripts

import kotlinx.coroutines.cancel
import net.lsafer.edgeseek.app.Local

context(local: Local)
fun registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(Thread {
        local.ioScope.cancel()
    })
}
