package com.example.face_lens.camera

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
internal fun FaceOverlay(
    faceBoxes: List<FaceBox>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.dp.toPx()
        val cornerRadius = 12.dp.toPx()

        faceBoxes.forEach { faceBox ->
            val width = faceBox.right - faceBox.left
            val height = faceBox.bottom - faceBox.top
            if (width > 0f && height > 0f) {
                drawRoundRect(
                    color = Color(0xFF55E68A),
                    topLeft = Offset(faceBox.left, faceBox.top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = strokeWidth),
                )
            }
        }
    }
}
