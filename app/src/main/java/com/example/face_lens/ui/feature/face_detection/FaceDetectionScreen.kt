package com.example.face_lens.ui.feature.face_detection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.face_lens.ui.feature.face_detection.viewmodel.FaceDetectionViewModel
import com.example.face_lens.utils.locale.LocaleUtils

@Composable
fun FaceDetectionScreen(
    viewModel: FaceDetectionViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FaceDetectionContent(
        uiState = uiState,
        language = LocaleUtils.currentLanguage(context),
        onFacesDetected = viewModel::onFacesDetected,
        onCameraUnavailable = viewModel::onCameraUnavailable,
        onRetryCamera = viewModel::onRetryCamera,
        onToggleLens = viewModel::onToggleLens,
        onToggleLanguage = { LocaleUtils.toggleLanguage(context) },
    )
}
