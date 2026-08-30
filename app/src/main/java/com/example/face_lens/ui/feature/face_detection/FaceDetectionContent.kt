package com.example.face_lens.ui.feature.face_detection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.face_lens.domain.model.DetectedFace
import com.example.face_lens.ui.feature.face_detection.components.CameraPreview
import com.example.face_lens.ui.feature.face_detection.components.CameraUnavailableContent
import com.example.face_lens.ui.feature.face_detection.components.FaceDetectionControls
import com.example.face_lens.ui.feature.face_detection.components.FaceDetectionOverlay
import com.example.face_lens.ui.feature.face_detection.components.PresenceLabel
import com.example.face_lens.ui.feature.face_detection.viewmodel.FaceDetectionUiState
import com.example.face_lens.utils.locale.AppLanguage

@Composable
internal fun FaceDetectionContent(
    uiState: FaceDetectionUiState,
    language: AppLanguage,
    onFacesDetected: (List<DetectedFace>) -> Unit,
    onCameraUnavailable: () -> Unit,
    onRetryCamera: () -> Unit,
    onToggleLens: () -> Unit,
    onToggleLanguage: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        key(uiState.sessionGeneration, uiState.lensFacing) {
            if (!uiState.cameraUnavailable) {
                CameraPreview(
                    lensFacing = uiState.lensFacing,
                    onFacesDetected = onFacesDetected,
                    onCameraUnavailable = onCameraUnavailable,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        if (uiState.cameraUnavailable) {
            CameraUnavailableContent(onRetry = onRetryCamera)
            return@Box
        }

        FaceDetectionOverlay(
            faces = uiState.detectedFaces,
            modifier = Modifier.fillMaxSize(),
        )
        PresenceLabel(
            presence = uiState.presence,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 16.dp),
        )
        FaceDetectionControls(
            lensFacing = uiState.lensFacing,
            language = language,
            onToggleLens = onToggleLens,
            onToggleLanguage = onToggleLanguage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = 24.dp),
        )
    }
}
