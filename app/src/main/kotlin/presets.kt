package net.lsafer.edgeseek.app

import net.lsafer.edgeseek.app.data.settings.*

val PRESET_SIDE_STANDARD = listOf(
    EdgeSideData(
        id = EdgeSide.Top,
        nSegments = 1,
    ),
    EdgeSideData(
        id = EdgeSide.Bottom,
        nSegments = 1,
    ),
    EdgeSideData(
        id = EdgeSide.Left,
        nSegments = 1,
    ),
    EdgeSideData(
        id = EdgeSide.Right,
        nSegments = 1,
    ),
)

val PRESET_SIDE_CENTERED = listOf(
    EdgeSideData(
        id = EdgeSide.Top,
        nSegments = 1,
    ),
    EdgeSideData(
        id = EdgeSide.Bottom,
        nSegments = 1,
    ),
    EdgeSideData(
        id = EdgeSide.Left,
        nSegments = 3,
    ),
    EdgeSideData(
        id = EdgeSide.Right,
        nSegments = 3,
    ),
)

val PRESET_POS_STANDARD = listOf(
    // Left
    EdgePosData(
        id = EdgePos.LeftCenter,
        activated = true,
        onSeek = ControlFeature.Music
    ),
    // Right
    EdgePosData(
        id = EdgePos.RightCenter,
        activated = true,
        onSeek = ControlFeature.Brightness
    ),
)

val PRESET_POS_BRIGHTNESS_ONLY = listOf(
    // Right
    EdgePosData(
        id = EdgePos.RightCenter,
        activated = true,
        onSeek = ControlFeature.Brightness
    ),
)

val PRESET_POS_DOUBLE_BRIGHTNESS = listOf(
    // Left
    EdgePosData(
        id = EdgePos.LeftCenter,
        activated = true,
        onSeek = ControlFeature.Brightness
    ),
    // Right
    EdgePosData(
        id = EdgePos.RightCenter,
        activated = true,
        onSeek = ControlFeature.Brightness
    ),
)
