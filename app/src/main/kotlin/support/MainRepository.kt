package net.lsafer.edgeseek.app.support

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import net.lsafer.edgeseek.app.data.settings.EdgePos
import net.lsafer.edgeseek.app.data.settings.EdgePosData
import net.lsafer.edgeseek.app.data.settings.EdgeSide
import net.lsafer.edgeseek.app.data.settings.EdgeSideData
import org.cufy.json.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class MainRepository(
    initial: JsonObject,
    private val onFlush: (JsonObject) -> Unit,
    private val coroutineScope: CoroutineScope,
) {
    private companion object {
        const val PK_FLAG_ACTIVATED = "f.activated"
        const val PK_FLAG_AUTO_BOOT = "f.auto_boot"
        const val PK_FLAG_BRIGHTNESS_RESET = "f.brightness_reset"

        const val PK_UI_LANG = "ui.lang"
        const val PK_UI_COLORS = "ui.colors"

        const val PK_WIZ_INTRO = "wiz.intro"
    }

    private val data = mutableStateMapOf<String, JsonElement>()
        .apply { putAll(initial) }

    init {
        snapshotFlow { data }
            .debounce(1.seconds)
            .onEach { flush() }
            .onCompletion { flush() }
            .launchIn(coroutineScope)
    }

    fun flush() {
        onFlush(data.toJsonObject())
    }

    // ==========[ UI ]

    var uiLang by createCachedProperty(
        get = { data[PK_UI_LANG]?.asStringOrNull },
        set = { data[PK_UI_LANG] = it },
    )
    var uiColors by createCachedProperty(
        get = { data[PK_UI_COLORS]?.asStringOrNull },
        set = { data[PK_UI_COLORS] = it },
    )

    // ==========[ WIZ ]

    var introduced by createCachedProperty(
        get = { data[PK_WIZ_INTRO]?.asBooleanOrNull ?: false },
        set = { data[PK_WIZ_INTRO] = it },
    )

    // ==========[ EDGE DATA ]

    var activated by createCachedProperty(
        get = { data[PK_FLAG_ACTIVATED]?.asBooleanOrNull ?: false },
        set = { data[PK_FLAG_ACTIVATED] = it },
    )

    var autoBoot by createCachedProperty(
        get = { data[PK_FLAG_AUTO_BOOT]?.asBooleanOrNull ?: true },
        set = { data[PK_FLAG_AUTO_BOOT] = it },
    )

    var brightnessReset by createCachedProperty(
        get = { data[PK_FLAG_BRIGHTNESS_RESET]?.asBooleanOrNull ?: true },
        set = { data[PK_FLAG_BRIGHTNESS_RESET] = it },
    )

    var edgePosList by createCachedProperty(
        get = {
            EdgePos.entries.map {
                data[it.key]
                    ?.deserializeOrNull()
                    ?: EdgePosData(it)
            }
        },
        set = {
            it.forEach {
                data[it.pos.key] = it.serializeToJsonElement()
            }
        }
    )

    var edgeSideList by createCachedProperty(
        get = {
            EdgeSide.entries.map {
                data[it.key]
                    ?.deserializeOrNull()
                    ?: EdgeSideData(it)
            }
        },
        set = {
            it.forEach {
                data[it.side.key] = it.serializeToJsonElement()
            }
        },
    )
}

private inline fun <T> createCachedProperty(
    crossinline get: () -> T,
    crossinline set: (T) -> Unit,
) = object : ReadWriteProperty<Any?, T> {
    private val _cache by derivedStateOf { get() }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = _cache
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}
