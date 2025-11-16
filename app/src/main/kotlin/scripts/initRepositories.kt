package net.lsafer.edgeseek.app.scripts

import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.lsafer.edgeseek.app.Local
import org.cufy.json.*
import java.io.File
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
context(local: Local)
fun initRepositories(file: File) {
    local.repo.data += file.readJson()
    snapshotFlow { local.repo.data.toMap() }
        .debounce(1.seconds)
        .onEach { file.writeJson(it) }
        .launchIn(local.ioScope)
}

private fun File.readJson(): JsonObjectLike {
    return try {
        readText().decodeJsonOrNull()?.asJsonObjectOrNull ?: JsonObject()
    } catch (e: Exception) {
        e.printStackTrace()
        return JsonObject()
    }
}

private fun File.writeJson(value: JsonObjectLike) {
    try {
        parentFile?.mkdir()
        writeText(value.serializeToJsonString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
