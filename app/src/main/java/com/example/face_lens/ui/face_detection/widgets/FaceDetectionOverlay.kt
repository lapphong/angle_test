package com.example.face_lens.ui.face_detection.widgets

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.face_lens.domain.model.DetectedFace
import kotlin.math.min

@Composable
fun FaceDetectionOverlay(
    faces: List<DetectedFace>,
    modifier: Modifier = Modifier,
) {
    val textPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }

    Canvas(modifier = modifier) {
        val boxStroke = 2.dp.toPx()
        val landmarkRadius = 3.dp.toPx()
        val boxCornerRadius = 8.dp.toPx()

        faces.forEach { face ->
            val bounds = face.bounds
            if (bounds.width <= 0f || bounds.height <= 0f) return@forEach

            drawRoundRect(
                color = Color.Red,
                topLeft = Offset(bounds.left, bounds.top),
                size = Size(bounds.width, bounds.height),
                cornerRadius = CornerRadius(boxCornerRadius, boxCornerRadius),
                style = Stroke(width = boxStroke),
            )

            face.landmarks.forEach { landmark ->
                drawCircle(
                    color = Color(0xFF2196F3),
                    radius = landmarkRadius,
                    center = Offset(landmark.x, landmark.y),
                )
            }

            face.smilingProbability?.let { probability ->
                val percent = (probability * 100).toInt()
                val label = when {
                    percent > 70 -> "😊 Happy ($percent%)"
                    percent > 40 -> "😐 Neutral ($percent%)"
                    else -> "😞 Sad ($percent%)"
                }
                val textColor = when {
                    percent > 70 -> android.graphics.Color.rgb(76, 175, 80)
                    percent > 40 -> android.graphics.Color.rgb(255, 214, 0)
                    else -> android.graphics.Color.rgb(244, 67, 54)
                }
                textPaint.textSize = 14.sp.toPx()
                textPaint.color = textColor
                val horizontalPadding = 8.dp.toPx()
                val verticalPadding = 6.dp.toPx()
                val textWidth = textPaint.measureText(label)
                val labelHeight = textPaint.fontMetrics.run { bottom - top }
                val maxLabelLeft = (size.width - textWidth - horizontalPadding * 2)
                    .coerceAtLeast(0f)
                val labelLeft = bounds.left.coerceIn(0f, maxLabelLeft)
                val labelBottom = (bounds.top - 8.dp.toPx()).coerceAtLeast(labelHeight + verticalPadding * 2)

                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.66f),
                    topLeft = Offset(
                        labelLeft,
                        labelBottom - labelHeight - verticalPadding * 2,
                    ),
                    size = Size(
                        textWidth + horizontalPadding * 2,
                        labelHeight + verticalPadding * 2,
                    ),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                )
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    labelLeft + horizontalPadding,
                    labelBottom - verticalPadding - textPaint.fontMetrics.bottom,
                    textPaint,
                )
            }
        }

        val horizontalMargin = if (size.width > 375.dp.toPx()) 24.dp.toPx() else 12.dp.toPx()
        val guideSize = min(size.width - horizontalMargin * 2, size.height * 0.52f)
        val guideLeft = (size.width - guideSize) / 2
        val guideTop = (size.height - guideSize) / 2
        val guideRect = Rect(
            offset = Offset(guideLeft, guideTop),
            size = Size(guideSize, guideSize),
        )
        val maskColor = Color.Black.copy(alpha = 0.60f)

        drawRect(maskColor, topLeft = Offset.Zero, size = Size(size.width, guideRect.top))
        drawRect(
            maskColor,
            topLeft = Offset(0f, guideRect.bottom),
            size = Size(size.width, size.height - guideRect.bottom),
        )
        drawRect(
            maskColor,
            topLeft = Offset(0f, guideRect.top),
            size = Size(guideRect.left, guideRect.height),
        )
        drawRect(
            maskColor,
            topLeft = Offset(guideRect.right, guideRect.top),
            size = Size(size.width - guideRect.right, guideRect.height),
        )

        val cornerLength = 30.dp.toPx()
        val radius = 18.dp.toPx()
        val cornerStroke = Stroke(width = 4.dp.toPx())

        fun drawCorner(origin: Offset, xDirection: Float, yDirection: Float) {
            val path = Path().apply {
                moveTo(origin.x, origin.y + yDirection * cornerLength)
                lineTo(origin.x, origin.y + yDirection * radius)
                quadraticTo(
                    origin.x,
                    origin.y,
                    origin.x + xDirection * radius,
                    origin.y,
                )
                lineTo(origin.x + xDirection * cornerLength, origin.y)
            }
            drawPath(path = path, color = Color.White, style = cornerStroke)
        }

        drawCorner(guideRect.topLeft, xDirection = 1f, yDirection = 1f)
        drawCorner(guideRect.topRight, xDirection = -1f, yDirection = 1f)
        drawCorner(guideRect.bottomRight, xDirection = -1f, yDirection = -1f)
        drawCorner(guideRect.bottomLeft, xDirection = 1f, yDirection = -1f)
    }
}
