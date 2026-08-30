package com.example.face_lens.ui.face_detection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.face_lens.R
import com.example.face_lens.domain.model.DetectedFace
import com.example.face_lens.ui.face_detection.widgets.CameraPreview
import com.example.face_lens.ui.face_detection.widgets.FaceDetectionOverlay

@Composable
fun FaceDetectionScreen(
    viewModel: FaceDetectionViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FaceDetectionContent(
        uiState = uiState,
        onFacesDetected = viewModel::onFacesDetected,
        onCameraUnavailable = viewModel::onCameraUnavailable,
        onSwitchCamera = viewModel::onSwitchCamera,
        onRetryCamera = viewModel::onRetryCamera,
    )
}

@Composable
private fun FaceDetectionContent(
    uiState: FaceDetectionUiState,
    onFacesDetected: (List<DetectedFace>) -> Unit,
    onCameraUnavailable: () -> Unit,
    onSwitchCamera: () -> Unit,
    onRetryCamera: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        key(uiState.cameraLens, uiState.sessionGeneration) {
            if (!uiState.cameraUnavailable) {
                CameraPreview(
                    cameraLens = uiState.cameraLens,
                    onFacesDetected = onFacesDetected,
                    onCameraUnavailable = onCameraUnavailable,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        if (uiState.cameraUnavailable) {
            CameraUnavailableContent(onRetry = onRetryCamera)
        } else {
            FaceDetectionOverlay(
                faces = uiState.detectedFaces,
                modifier = Modifier.fillMaxSize(),
            )
            PresenceLabel(
                presence = uiState.presence,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 76.dp),
            )
        }

        CameraSwitchButton(
            onClick = onSwitchCamera,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(20.dp),
        )

        ScreenHeader(
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun ScreenHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF2196F3))
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.screen_title),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun PresenceLabel(
    presence: Presence,
    modifier: Modifier = Modifier,
) {
    val personPresent = presence == Presence.PERSON_PRESENT
    val accentColor = if (personPresent) Color(0xFF55E68A) else Color(0xFFFF6B6B)

    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.72f),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(accentColor, CircleShape),
        )
        Text(
            text = stringResource(
                if (personPresent) {
                    R.string.status_person_present
                } else {
                    R.string.status_no_person
                },
            ),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CameraSwitchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.camera_switch)
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = description },
        containerColor = Color(0xFFD7E3FF),
        contentColor = Color(0xFF001B3F),
    ) {
        Text(
            text = "⇄",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CameraUnavailableContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.camera_unavailable),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 20.dp),
        ) {
            Text(stringResource(R.string.camera_retry))
        }
    }
}
