package com.example.face_lens.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.face_lens.domain.model.DetectedFace

@Composable
fun FaceDetectionOverlay(
    faces: List<DetectedFace>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.dp.toPx()
        val cornerRadius = 12.dp.toPx()

        faces.forEach { face ->
            val bounds = face.bounds
            if (bounds.width > 0f && bounds.height > 0f) {
                drawRoundRect(
                    color = Color(0xFF55E68A),
                    topLeft = Offset(bounds.left, bounds.top),
                    size = Size(bounds.width, bounds.height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = strokeWidth),
                )
            }
        }
    }
}
