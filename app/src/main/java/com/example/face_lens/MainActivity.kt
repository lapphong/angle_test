package com.example.face_lens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.face_lens.ui.feature.face_detection.FaceDetectionScreen
import com.example.face_lens.utils.permission.CameraPermissionGate
import com.example.face_lens.ui.theme.FaceLensTheme
import com.example.face_lens.utils.locale.LocaleUtils

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.localizedContext(newBase))
    }

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
