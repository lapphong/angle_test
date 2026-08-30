package com.example.face_lens.ui.face_detection.widgets

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
import com.example.face_lens.domain.model.CameraLens
import com.example.face_lens.domain.model.DetectedFace
import com.example.face_lens.domain.model.FaceBounds
import com.example.face_lens.domain.model.FacePoint
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

private val landmarkTypes = listOf(
    FaceLandmark.LEFT_EYE,
    FaceLandmark.RIGHT_EYE,
    FaceLandmark.NOSE_BASE,
    FaceLandmark.MOUTH_LEFT,
    FaceLandmark.MOUTH_RIGHT,
    FaceLandmark.MOUTH_BOTTOM,
)

@Composable
fun CameraPreview(
    cameraLens: CameraLens,
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
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.10f)
                .enableTracking()
                .build(),
        )
    }
    val cameraController = remember(context, cameraLens) {
        LifecycleCameraController(context).apply {
            cameraSelector = if (cameraLens == CameraLens.FRONT) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
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
                        landmarks = landmarkTypes.mapNotNull { landmarkType ->
                            face.getLandmark(landmarkType)?.position?.let { point ->
                                FacePoint(x = point.x, y = point.y)
                            }
                        },
                        smilingProbability = face.smilingProbability,
                        trackingId = face.trackingId,
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
