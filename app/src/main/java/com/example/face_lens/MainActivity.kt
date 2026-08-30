package com.example.face_lens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.face_lens.core.permission.CameraPermissionGate
import com.example.face_lens.ui.FaceDetectionScreen
import com.example.face_lens.ui.theme.FaceLensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaceLensTheme {
                CameraPermissionGate {
                    FaceDetectionScreen()
                }
            }
        }
    }
}
