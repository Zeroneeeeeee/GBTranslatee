package gambi.zerone.gbtranslate.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object RecentLanguagePrefs {

    private const val PREF_NAME = "recent_language_prefs"
    private const val KEY_RECENT_LANGUAGES = "recent_languages"
    private const val MAX_SIZE = 3

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun save(context: Context, languageCode: String) {
        val pref = prefs(context)

        val current = pref.getString(KEY_RECENT_LANGUAGES, "")
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toMutableList()
            ?: mutableListOf()

        current.remove(languageCode)

        current.add(0, languageCode)

        pref.edit {
            putString(
                KEY_RECENT_LANGUAGES,
                current.take(MAX_SIZE).joinToString(",")
            )
        }
    }

    fun get(context: Context): List<String> {
        val value = prefs(context)
            .getString(KEY_RECENT_LANGUAGES, "")
            ?: ""

        if (value.isBlank()) return emptyList()

        return value.split(",")
            .filter { it.isNotBlank() }
    }

    fun clear(context: Context) {
        prefs(context).edit {
            remove(KEY_RECENT_LANGUAGES)
        }
    }
}
