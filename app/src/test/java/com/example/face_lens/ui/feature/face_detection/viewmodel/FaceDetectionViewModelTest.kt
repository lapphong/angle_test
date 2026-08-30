package com.example.face_lens.ui.feature.face_detection.viewmodel

import com.example.face_lens.domain.model.DetectedFace
import com.example.face_lens.domain.model.FaceBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FaceDetectionViewModelTest {
    @Test
    fun `camera starts with front lens`() {
        val viewModel = FaceDetectionViewModel()

        assertEquals(LensFacing.FRONT, viewModel.uiState.value.lensFacing)
    }

    @Test
    fun `toggle lens switches between front and back and clears stale faces`() {
        val viewModel = FaceDetectionViewModel()
        viewModel.onFacesDetected(listOf(detectedFace()))

        viewModel.onToggleLens()

        assertEquals(LensFacing.BACK, viewModel.uiState.value.lensFacing)
        assertTrue(viewModel.uiState.value.detectedFaces.isEmpty())

        viewModel.onToggleLens()

        assertEquals(LensFacing.FRONT, viewModel.uiState.value.lensFacing)
    }

    @Test
    fun `camera error can be retried with a new session`() {
        val viewModel = FaceDetectionViewModel()
        viewModel.onCameraUnavailable()

        assertTrue(viewModel.uiState.value.cameraUnavailable)

        viewModel.onRetryCamera()

        assertFalse(viewModel.uiState.value.cameraUnavailable)
        assertEquals(1, viewModel.uiState.value.sessionGeneration)
    }

    private fun detectedFace() = DetectedFace(
        bounds = FaceBounds(left = 10f, top = 20f, right = 110f, bottom = 140f),
    )
}
