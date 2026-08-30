package com.example.face_lens.utils.locale

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun `language toggle switches from Vietnamese to English`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.VIETNAMESE.next())
    }

    @Test
    fun `language toggle switches from English to Vietnamese`() {
        assertEquals(AppLanguage.VIETNAMESE, AppLanguage.ENGLISH.next())
    }
}
