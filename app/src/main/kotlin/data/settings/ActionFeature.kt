package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ActionFeature {
    @Serializable
    @SerialName("nothing")
    data object Nothing : ActionFeature
    @Serializable
    @SerialName("expand_status_bar")
    data object ExpandStatusBar : ActionFeature
}
