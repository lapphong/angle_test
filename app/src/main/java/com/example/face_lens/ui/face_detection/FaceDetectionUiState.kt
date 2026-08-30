package com.example.face_lens.ui.face_detection

import com.example.face_lens.domain.model.CameraLens
import com.example.face_lens.domain.model.DetectedFace

data class FaceDetectionUiState(
    val detectedFaces: List<DetectedFace> = emptyList(),
    val cameraLens: CameraLens = CameraLens.FRONT,
    val cameraUnavailable: Boolean = false,
    val sessionGeneration: Int = 0,
) {
    val presence: Presence
        get() = if (detectedFaces.isEmpty()) Presence.NO_PERSON else Presence.PERSON_PRESENT
}

enum class Presence {
    PERSON_PRESENT,
    NO_PERSON,
}
