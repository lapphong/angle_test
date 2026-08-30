package com.example.face_lens.ui.face_detection

import androidx.lifecycle.ViewModel
import com.example.face_lens.domain.model.CameraLens
import com.example.face_lens.domain.model.DetectedFace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FaceDetectionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FaceDetectionUiState())
    val uiState: StateFlow<FaceDetectionUiState> = _uiState.asStateFlow()

    fun onFacesDetected(faces: List<DetectedFace>) {
        _uiState.update { state -> state.copy(detectedFaces = faces) }
    }

    fun onSwitchCamera() {
        _uiState.update { state ->
            state.copy(
                detectedFaces = emptyList(),
                cameraLens = if (state.cameraLens == CameraLens.FRONT) {
                    CameraLens.BACK
                } else {
                    CameraLens.FRONT
                },
                cameraUnavailable = false,
                sessionGeneration = state.sessionGeneration + 1,
            )
        }
    }

    fun onCameraUnavailable() {
        _uiState.update { state ->
            state.copy(detectedFaces = emptyList(), cameraUnavailable = true)
        }
    }

    fun onRetryCamera() {
        _uiState.update { state ->
            state.copy(
                detectedFaces = emptyList(),
                cameraUnavailable = false,
                sessionGeneration = state.sessionGeneration + 1,
            )
        }
    }
}
