package net.lsafer.edgeseek.app.scripts

import kotlinx.serialization.json.JsonObject
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.support.MainRepository
import okio.Path
import org.cufy.json.asJsonObjectOrNull
import org.cufy.json.decodeJsonOrNull
import org.cufy.json.encodeToString
import org.cufy.json.JsonObject
import java.io.File

context(local: Local)
fun initAndroidRepositories(dataDir: Path) {
    val file = dataDir.resolve("datastore.json").toFile()

    local.repo = MainRepository(
        initial = file.tryReadJsonObject() ?: JsonObject(),
        onFlush = { file.tryWriteJsonObject(it) },
        coroutineScope = local.ioScope,
    )
}

private fun File.tryReadJsonObject(): JsonObject? {
    return try {
        readText().decodeJsonOrNull()?.asJsonObjectOrNull
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

private fun File.tryWriteJsonObject(value: JsonObject) {
    try {
        parentFile?.mkdir()
        writeText(value.encodeToString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
