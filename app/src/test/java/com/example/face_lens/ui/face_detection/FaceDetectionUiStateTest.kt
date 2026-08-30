package com.example.face_lens.ui.face_detection

import com.example.face_lens.domain.model.CameraLens
import com.example.face_lens.domain.model.DetectedFace
import com.example.face_lens.domain.model.FaceBounds
import org.junit.Assert.assertEquals
import org.junit.Test

class FaceDetectionUiStateTest {
    @Test
    fun `empty face list means no person`() {
        assertEquals(Presence.NO_PERSON, FaceDetectionUiState().presence)
    }

    @Test
    fun `one or more detected faces means person present`() {
        val state = FaceDetectionUiState(
            detectedFaces = listOf(
                DetectedFace(
                    bounds = FaceBounds(left = 10f, top = 20f, right = 110f, bottom = 140f),
                ),
            ),
        )

        assertEquals(Presence.PERSON_PRESENT, state.presence)
    }

    @Test
    fun `switch camera toggles lens and starts a new session`() {
        val viewModel = FaceDetectionViewModel()

        viewModel.onSwitchCamera()

        assertEquals(CameraLens.BACK, viewModel.uiState.value.cameraLens)
        assertEquals(1, viewModel.uiState.value.sessionGeneration)
    }
}
