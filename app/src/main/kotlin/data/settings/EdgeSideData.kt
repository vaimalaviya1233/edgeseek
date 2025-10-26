package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.Serializable

@Serializable
data class EdgeSideData(
    val side: EdgeSide,
    val nSegments: Int = when (side) {
        EdgeSide.Top, EdgeSide.Bottom -> 2
        EdgeSide.Left, EdgeSide.Right -> 3
    },
)
