package com.example.face_lens.utils.locale

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import com.example.face_lens.utils.findActivity
import java.util.Locale

enum class AppLanguage(val languageTag: String) {
    VIETNAMESE("vi"),
    ENGLISH("en"),
    ;

    fun next(): AppLanguage = when (this) {
        VIETNAMESE -> ENGLISH
        ENGLISH -> VIETNAMESE
    }
}

object LocaleUtils {
    private const val PREFERENCES_NAME = "locale_preferences"
    private const val LANGUAGE_KEY = "language"

    fun localizedContext(context: Context): Context {
        val locale = Locale.forLanguageTag(currentLanguage(context).languageTag)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        return context.createConfigurationContext(configuration)
    }

    fun currentLanguage(context: Context): AppLanguage {
        val savedLanguage = preferences(context).getString(
            LANGUAGE_KEY,
            AppLanguage.VIETNAMESE.languageTag,
        )
        return AppLanguage.entries.firstOrNull { it.languageTag == savedLanguage }
            ?: AppLanguage.VIETNAMESE
    }

    fun toggleLanguage(context: Context) {
        val nextLanguage = currentLanguage(context).next()
        preferences(context).edit {
            putString(LANGUAGE_KEY, nextLanguage.languageTag)
        }
        context.findActivity()?.recreate()
    }

    private fun preferences(context: Context) = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
}
