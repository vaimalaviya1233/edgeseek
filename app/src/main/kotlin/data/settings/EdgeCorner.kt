package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.Serializable

@Serializable
enum class EdgeCorner {
    BottomRight, Bottom, BottomLeft, Left,
    TopLeft, Top, TopRight, Right;

    fun rotate(rotation: Int) =
        entries[(16 + ordinal - (rotation % 8) * 2) % 8]
}
