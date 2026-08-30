package com.example.face_lens.ui.view_model

import com.example.face_lens.domain.model.DetectedFace

data class FaceDetectionUiState(
    val detectedFaces: List<DetectedFace> = emptyList(),
    val cameraUnavailable: Boolean = false,
    val sessionGeneration: Int = 0,
    val lensFacing: LensFacing = LensFacing.FRONT,
) {
    val presence: Presence
        get() = if (detectedFaces.isEmpty()) Presence.NO_PERSON else Presence.PERSON_PRESENT
}

enum class Presence {
    PERSON_PRESENT,
    NO_PERSON,
}

enum class LensFacing {
    FRONT,
    BACK,
}
