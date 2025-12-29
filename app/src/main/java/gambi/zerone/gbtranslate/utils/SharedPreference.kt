package gambi.zerone.gbtranslate.utils

import android.content.Context
import androidx.core.content.edit
import gambi.zerone.gbtranslate.view.setting.UiMode

object SharedPreference {
    private const val LANG_NAME = "MyPref"

    fun saveLanguage(context: Context, language: String) {
        val pref = context.getSharedPreferences(LANG_NAME, Context.MODE_PRIVATE)
        pref.edit { putString("language", language) }
    }

    fun getLanguage(context: Context): String? {
        val pref = context.getSharedPreferences(LANG_NAME, Context.MODE_PRIVATE)
        return pref.getString("language", null)
    }

    fun saveUiMode(context: Context, mode: UiMode) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        prefs.edit {
            putString("ui_mode", mode.name)
        }
    }

    fun getUiMode(context: Context): UiMode {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val value = prefs.getString("ui_mode", null)

        return when (value) {
            UiMode.DARK.name -> UiMode.DARK
            UiMode.LIGHT.name -> UiMode.LIGHT
            else -> UiMode.LIGHT // mặc định
        }
    }

}