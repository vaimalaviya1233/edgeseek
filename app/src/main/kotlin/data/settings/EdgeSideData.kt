package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdgeSideData(
    @SerialName("side")
    val id: EdgeSide,
    val nSegments: Int = when (id) {
        EdgeSide.Top, EdgeSide.Bottom -> 2
        EdgeSide.Left, EdgeSide.Right -> 3
    },
)
