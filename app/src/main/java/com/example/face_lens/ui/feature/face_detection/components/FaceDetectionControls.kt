package com.example.face_lens.ui.feature.face_detection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.face_lens.R
import com.example.face_lens.ui.feature.face_detection.viewmodel.LensFacing
import com.example.face_lens.utils.locale.AppLanguage

@Composable
internal fun FaceDetectionControls(
    lensFacing: LensFacing,
    language: AppLanguage,
    onToggleLens: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onToggleLens,
            shape = RoundedCornerShape(50),
        ) {
            val targetCamera = stringResource(
                if (lensFacing == LensFacing.FRONT) R.string.camera_back else R.string.camera_front,
            )
            Text(text = "↻  $targetCamera")
        }

        val languageDescription = stringResource(R.string.language_switch)
        OutlinedButton(
            onClick = onToggleLanguage,
            modifier = Modifier.semantics { contentDescription = languageDescription },
            shape = RoundedCornerShape(50),
        ) {
            Text(
                text = if (language == AppLanguage.VIETNAMESE) "EN" else "VI",
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    }
}
