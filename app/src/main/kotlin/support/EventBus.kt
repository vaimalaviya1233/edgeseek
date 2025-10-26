package net.lsafer.edgeseek.app.support

import kotlinx.coroutines.channels.Channel

class EventBus {
    // Upstream
    val openUrl = Channel<String>()
    val startService = Channel<Unit>() // todo replace this with snapshotFlow { ... }
}
