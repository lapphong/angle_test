package com.example.face_lens.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.face_lens.R

@Composable
fun CameraScreen() {
    var state by remember { mutableStateOf(CameraUiState()) }
    var cameraSessionKey by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        key(cameraSessionKey) {
            if (!state.cameraUnavailable) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onFacesDetected = { faceBoxes ->
                        state = state.copy(faceBoxes = faceBoxes)
                    },
                    onCameraUnavailable = {
                        state = CameraUiState(cameraUnavailable = true)
                    },
                )
            }
        }

        if (state.cameraUnavailable) {
            CameraUnavailableContent(
                onRetry = {
                    state = CameraUiState()
                    cameraSessionKey++
                },
            )
        } else {
            FaceOverlay(
                faceBoxes = state.faceBoxes,
                modifier = Modifier.fillMaxSize(),
            )
            PresenceLabel(
                presence = state.presence,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(top = 16.dp),
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
