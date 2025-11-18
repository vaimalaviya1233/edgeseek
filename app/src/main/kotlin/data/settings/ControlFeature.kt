package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ControlFeature {
    @Serializable
    @SerialName("nothing")
    data object Nothing : ControlFeature
    @Serializable
    @SerialName("brightness")
    data object Brightness : ControlFeature
    @Serializable
    @SerialName("brightness_dimmer")
    data object BrightnessWithDimmer : ControlFeature
    @Serializable
    @SerialName("alarm")
    data object Alarm : ControlFeature
    @Serializable
    @SerialName("music")
    data object Music : ControlFeature
    @Serializable
    @SerialName("ring")
    data object Ring : ControlFeature
    @Serializable
    @SerialName("system")
    data object System : ControlFeature
}
