package net.lsafer.edgeseek.app.support

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.serialization.json.JsonElement
import net.lsafer.edgeseek.app.data.settings.*
import org.cufy.json.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * In-memory key–value repository backed by a Compose `mutableStateMapOf`.
 *
 * All values update reactively through Compose Snapshot System.
 *
 * At application startup, the repository is hydrated from disk
 * and then kept in sync automatically: whenever the internal `data` map changes,
 * a snapshot of the map is written back to disk from a background I/O scope.
 * This creates a simple persistent data store without needing SharedPreferences
 * or DataStore, while still being fully reactive inside the Compose runtime.
 */
class Repo {
    companion object {
        private const val PK_FLAG_ACTIVATED = "f.activated"
        private const val PK_FLAG_AUTO_BOOT = "f.auto_boot"
        private const val PK_FLAG_BRIGHTNESS_RESET = "f.brightness_reset"
        private const val PK_FLAG_ROTATE_EDGES = "f.rotate_edges"

        private const val PK_UI_LANG = "ui.lang"
        private const val PK_UI_COLORS = "ui.colors"

        private const val PK_WIZ_INTRO = "wiz.intro"
    }

    /** Internal reactive storage for all JSON-encoded values. */
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

    /** Whether the user has completed the introductory wizard. */
    var introduced by property(
        get = { data[PK_WIZ_INTRO]?.asBooleanOrNull ?: false },
        set = { data[PK_WIZ_INTRO] = it },
    )

    // ==========[ FUN ]

    /** Whether the main feature is currently activated. */
    var activated by property(
        get = { data[PK_FLAG_ACTIVATED]?.asBooleanOrNull ?: false },
        set = { data[PK_FLAG_ACTIVATED] = it },
    )

    /** Whether auto-boot is enabled. Defaults to `true` if unset. */
    var autoBoot by property(
        get = { data[PK_FLAG_AUTO_BOOT]?.asBooleanOrNull ?: true },
        set = { data[PK_FLAG_AUTO_BOOT] = it },
    )

    /** Whether brightness should be reset on screen off. */
    var brightnessReset by property(
        get = { data[PK_FLAG_BRIGHTNESS_RESET]?.asBooleanOrNull ?: true },
        set = { data[PK_FLAG_BRIGHTNESS_RESET] = it },
    )

    /** True, to rotate edges with the rotation of the display. */
    var rotateEdges by property(
        get = { data[PK_FLAG_ROTATE_EDGES]?.asBooleanOrNull ?: false },
        set = { data[PK_FLAG_ROTATE_EDGES] = it },
    )

    /**
     * Fixed-size list of edge-position configs.
     *
     * The list always contains one entry per `EdgePos` enum, regardless of what is
     * actually stored. Missing entries fall back to their default `EdgePosData`.
     */
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
                data[it.id.key] = it.serializeToJsonElement()
            }
        }
    )

    /**
     * Fixed-size list of edge-side configs.
     *
     * The list always contains one entry per `EdgeSide` enum, regardless of what is
     * actually stored. Missing entries fall back to their default `EdgeSideData`.
     */
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
                data[it.id.key] = it.serializeToJsonElement()
            }
        },
    )

    /**
     * Retrieves the stored configuration for a specific edge side,
     * returning a default instance if none was saved.
     *
     * > This lookup is Snapshot-aware
     */
    fun getEdgeSideData(side: EdgeSide) =
        edgeSideList.find { it.id == side } ?: EdgeSideData(side)

    /**
     * Retrieves the stored configuration for a specific edge pos,
     * returning a default instance if none was saved.
     *
     * > This lookup is Snapshot-aware
     */
    fun getEdgePosData(pos: EdgePos) =
        edgePosList.find { it.id == pos } ?: EdgePosData(pos)

    /**
     * Retrieves the stored configuration for a specific edge,
     * returning a default instance if none was saved.
     *
     * > This lookup is Snapshot-aware
     */
    fun getEdgeData(pos: EdgePos) = EdgeData(getEdgeSideData(pos.side), getEdgePosData(pos))
}

/**
 * Creates a Compose-aware read/write property delegate that caches reads
 * via `derivedStateOf`, ensuring reactive updates when the underlying value changes.
 */
private fun <T> property(get: () -> T, set: (T) -> Unit) =
    object : ReadWriteProperty<Any?, T> {
        private val _cache by derivedStateOf { get() }
        override fun getValue(thisRef: Any?, property: KProperty<*>) = _cache
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
    }
