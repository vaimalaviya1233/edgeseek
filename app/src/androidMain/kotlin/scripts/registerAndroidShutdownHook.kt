package net.lsafer.edgeseek.app.scripts

import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import net.lsafer.edgeseek.app.Local

context(local: Local)
fun registerAndroidShutdownHook() {
    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            local.ioScope.cancel()
        }
    })
}
