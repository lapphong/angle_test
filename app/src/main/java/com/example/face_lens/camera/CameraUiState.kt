package com.example.face_lens.camera

data class FaceBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

data class CameraUiState(
    val faceBoxes: List<FaceBox> = emptyList(),
    val cameraUnavailable: Boolean = false,
) {
    val presence: Presence
        get() = if (faceBoxes.isEmpty()) Presence.NO_PERSON else Presence.PERSON_PRESENT
}

enum class Presence {
    PERSON_PRESENT,
    NO_PERSON,
}
