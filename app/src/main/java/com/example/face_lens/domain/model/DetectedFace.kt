package com.example.face_lens.domain.model

data class FaceBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}

data class DetectedFace(
    val bounds: FaceBounds,
)
