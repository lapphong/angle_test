package com.example.face_lens.utils.permission

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CameraPermissionGate(content: @Composable () -> Unit) {
    PermissionGate(
        permission = Manifest.permission.CAMERA,
        deniedContent = { actions ->
            CameraPermissionDeniedContent(
                canRequestAgain = actions.canRequestAgain,
                onRequestAgain = actions.requestAgain,
                onOpenSettings = actions.openSettings,
            )
        },
        pendingContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            )
        },
        content = content,
    )
}
