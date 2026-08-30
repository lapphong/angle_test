package com.example.face_lens.ui.view_model

import androidx.lifecycle.ViewModel
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

    fun onToggleLens() {
        _uiState.update { state ->
            if (state.cameraUnavailable) {
                state
            } else {
                state.copy(
                    detectedFaces = emptyList(),
                    lensFacing = if (state.lensFacing == LensFacing.FRONT) {
                        LensFacing.BACK
                    } else {
                        LensFacing.FRONT
                    },
                )
            }
        }
    }

}
