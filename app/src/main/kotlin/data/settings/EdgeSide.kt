package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.Serializable

@Serializable
enum class EdgeSide(val key: String) {
    Bottom("side_bottom"),
    Left("side_left"),
    Top("side_top"),
    Right("side_right");

    fun rotate(rotation: Int) =
        entries[(8 + ordinal - (rotation % 4) * 1) % 4]
}
