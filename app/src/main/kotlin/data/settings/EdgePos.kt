package net.lsafer.edgeseek.app.data.settings

import kotlinx.serialization.Serializable

@Serializable
enum class EdgePos(val key: String, val side: EdgeSide, val corner: EdgeCorner) {
    BottomRight("pos_bottom_right", EdgeSide.Bottom, EdgeCorner.BottomRight),
    BottomCenter("pos_bottom_center", EdgeSide.Bottom, EdgeCorner.Bottom),
    BottomLeft("pos_bottom_left", EdgeSide.Bottom, EdgeCorner.BottomLeft),
    LeftBottom("pos_left_bottom", EdgeSide.Left, EdgeCorner.BottomLeft),
    LeftCenter("pos_left_center", EdgeSide.Left, EdgeCorner.Left),
    LeftTop("pos_left_top", EdgeSide.Left, EdgeCorner.TopLeft),
    TopLeft("pos_top_left", EdgeSide.Top, EdgeCorner.TopLeft),
    TopCenter("pos_top_center", EdgeSide.Top, EdgeCorner.Top),
    TopRight("pos_top_right", EdgeSide.Top, EdgeCorner.TopRight),
    RightTop("pos_right_top", EdgeSide.Right, EdgeCorner.TopRight),
    RightCenter("pos_right_center", EdgeSide.Right, EdgeCorner.Right),
    RightBottom("pos_right_bottom", EdgeSide.Right, EdgeCorner.BottomRight);

    fun isIncludedWhenSegmented(nSegments: Int): Boolean {
        return when (this) {
            BottomCenter,
            TopCenter,
            RightCenter,
            LeftCenter,
            -> nSegments == 1 || nSegments == 3

            BottomLeft, BottomRight,
            TopLeft, TopRight,
            LeftTop, LeftBottom,
            RightTop, RightBottom,
            -> nSegments == 2 || nSegments == 3
        }
    }
}
