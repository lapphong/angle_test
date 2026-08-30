package com.example.face_lens.ui.feature.face_detection.components

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.face_lens.domain.model.DetectedFace
import com.example.face_lens.domain.model.FaceBounds
import com.example.face_lens.ui.feature.face_detection.viewmodel.LensFacing
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@Composable
internal fun CameraPreview(
    lensFacing: LensFacing,
    onFacesDetected: (List<DetectedFace>) -> Unit,
    onCameraUnavailable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnFacesDetected = rememberUpdatedState(onFacesDetected)
    val currentOnCameraUnavailable = rememberUpdatedState(onCameraUnavailable)
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val faceDetector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setMinFaceSize(0.10f)
                .enableTracking()
                .build(),
        )
    }
    val cameraController = remember(context, lensFacing) {
        LifecycleCameraController(context).apply {
            cameraSelector = when (lensFacing) {
                LensFacing.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
                LensFacing.BACK -> CameraSelector.DEFAULT_BACK_CAMERA
            }
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    AndroidView(
        factory = { viewContext ->
            PreviewView(viewContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                controller = cameraController
            }
        },
        modifier = modifier,
    )

    DisposableEffect(cameraController, faceDetector, lifecycleOwner) {
        val analyzer = MlKitAnalyzer(
            listOf(faceDetector),
            ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
            mainExecutor,
        ) { result ->
            val detectedFaces = result.getValue(faceDetector)
                ?.map { face ->
                    DetectedFace(
                        bounds = FaceBounds(
                            left = face.boundingBox.left.toFloat(),
                            top = face.boundingBox.top.toFloat(),
                            right = face.boundingBox.right.toFloat(),
                            bottom = face.boundingBox.bottom.toFloat(),
                        ),
                    )
                }
                .orEmpty()
            currentOnFacesDetected.value(detectedFaces)
        }

        try {
            cameraController.setImageAnalysisAnalyzer(mainExecutor, analyzer)
            cameraController.bindToLifecycle(lifecycleOwner)
        } catch (_: IllegalArgumentException) {
            currentOnCameraUnavailable.value()
        } catch (_: IllegalStateException) {
            currentOnCameraUnavailable.value()
        }

        onDispose {
            cameraController.clearImageAnalysisAnalyzer()
            cameraController.unbind()
            faceDetector.close()
        }
    }
}
