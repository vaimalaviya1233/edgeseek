package net.lsafer.edgeseek.app.scripts

import kotlinx.coroutines.cancel
import net.lsafer.edgeseek.app.Local

/**
 * Registers a JVM shutdown hook that cleans up app resources.
 */
context(local: Local)
fun registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(Thread {
        local.ioScope.cancel()
    })
}
