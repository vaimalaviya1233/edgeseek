package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrientationFilter {
    @SerialName("all")
    All,
    @SerialName("portrait_only")
    PortraitOnly,
    @SerialName("landscape_only")
    LandscapeOnly, ;

    fun test(displayRotation: Int): Boolean {
        return when (this) {
            All -> true
            PortraitOnly -> displayRotation % 2 == 0
            LandscapeOnly -> displayRotation % 2 == 1
        }
    }
}
