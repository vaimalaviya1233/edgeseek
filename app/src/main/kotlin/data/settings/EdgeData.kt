package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.Serializable

@Serializable
data class EdgeData(
    val side: EdgeSideData,
    val pos: EdgePosData,
)
