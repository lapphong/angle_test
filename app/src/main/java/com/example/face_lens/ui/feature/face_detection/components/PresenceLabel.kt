package com.example.face_lens.ui.feature.face_detection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.face_lens.R
import com.example.face_lens.ui.feature.face_detection.viewmodel.Presence

@Composable
internal fun PresenceLabel(
    presence: Presence,
    modifier: Modifier = Modifier,
) {
    val personPresent = presence == Presence.PERSON_PRESENT
    val accentColor = if (personPresent) Color(0xFF55E68A) else Color(0xFFFF6B6B)

    Text(
        text = stringResource(
            if (personPresent) R.string.status_person_present else R.string.status_no_person,
        ),
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.72f),
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        color = accentColor,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}
