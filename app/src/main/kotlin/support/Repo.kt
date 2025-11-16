package net.lsafer.edgeseek.app.support

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.JsonElement
import net.lsafer.edgeseek.app.data.settings.EdgePos
import net.lsafer.edgeseek.app.data.settings.EdgePosData
import net.lsafer.edgeseek.app.data.settings.EdgeSide
import net.lsafer.edgeseek.app.data.settings.EdgeSideData
import org.cufy.json.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@OptIn(FlowPreview::class)
class Repo {
    companion object {
        private const val PK_FLAG_ACTIVATED = "f.activated"
        private const val PK_FLAG_AUTO_BOOT = "f.auto_boot"
        private const val PK_FLAG_BRIGHTNESS_RESET = "f.brightness_reset"

        private const val PK_UI_LANG = "ui.lang"
        private const val PK_UI_COLORS = "ui.colors"

        private const val PK_WIZ_INTRO = "wiz.intro"
    }

    val data = mutableStateMapOf<String, JsonElement>()

    // ==========[ UI ]

    var uiLang by property(
        get = { data[PK_UI_LANG]?.asStringOrNull },
        set = { data[PK_UI_LANG] = it },
    )
    var uiColors by property(
        get = { data[PK_UI_COLORS]?.asStringOrNull },
        set = { data[PK_UI_COLORS] = it },
    )

    // ==========[ WIZ ]

    var introduced by property(
        get = { data[PK_WIZ_INTRO]?.asBooleanOrNull ?: false },
        set = { data[PK_WIZ_INTRO] = it },
    )

    // ==========[ FUN ]

    var activated by property(
        get = { data[PK_FLAG_ACTIVATED]?.asBooleanOrNull ?: false },
        set = { data[PK_FLAG_ACTIVATED] = it },
    )

    var autoBoot by property(
        get = { data[PK_FLAG_AUTO_BOOT]?.asBooleanOrNull ?: true },
        set = { data[PK_FLAG_AUTO_BOOT] = it },
    )

    var brightnessReset by property(
        get = { data[PK_FLAG_BRIGHTNESS_RESET]?.asBooleanOrNull ?: true },
        set = { data[PK_FLAG_BRIGHTNESS_RESET] = it },
    )

    var edgePosList by property(
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

    var edgeSideList by property(
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

    operator fun get(side: EdgeSide) =
        edgeSideList.find { it.side == side } ?: EdgeSideData(side)

    operator fun get(pos: EdgePos) =
        edgePosList.find { it.pos == pos } ?: EdgePosData(pos)
}

private fun <T> property(get: () -> T, set: (T) -> Unit) =
    object : ReadWriteProperty<Any?, T> {
        private val _cache by derivedStateOf { get() }
        override fun getValue(thisRef: Any?, property: KProperty<*>) = _cache
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
    }
