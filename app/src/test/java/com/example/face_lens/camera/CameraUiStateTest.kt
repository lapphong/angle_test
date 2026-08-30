package com.example.face_lens.camera

import org.junit.Assert.assertEquals
import org.junit.Test

class CameraUiStateTest {
    @Test
    fun `empty face list means no person`() {
        assertEquals(Presence.NO_PERSON, CameraUiState().presence)
    }

    @Test
    fun `one or more face boxes means person present`() {
        val state = CameraUiState(
            faceBoxes = listOf(
                FaceBox(left = 10f, top = 20f, right = 110f, bottom = 140f),
            ),
        )

        assertEquals(Presence.PERSON_PRESENT, state.presence)
    }
}
