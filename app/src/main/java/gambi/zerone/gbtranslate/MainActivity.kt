package gambi.zerone.gbtranslate

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import gambi.zerone.gbtranslate.ui.theme.GBTranslateTheme
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.view.setting.UiMode
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var theme by remember { mutableStateOf(SharedPreference.getUiMode(this)) }
            val context = LocalContext.current
            var language by remember {
                mutableStateOf(
                    SharedPreference.getLanguage(context) ?: "en"
                )
            }
            var currentLocale by remember {
                mutableStateOf(
                    Locale.Builder().setLanguage(language).build()
                )
            }

            val localizedContext =
                remember(currentLocale) { context.updateLocale(currentLocale) }
            GBTranslateTheme(
                darkTheme = when (theme) {
                    UiMode.LIGHT -> false
                    UiMode.DARK -> true
                }
            ) {
                Navigation(
                    application = application,
                    activity = this,
                    localizedContext = localizedContext,
                    getLocale = {
                        currentLocale = Locale.Builder()
                            .setLanguage(SharedPreference.getLanguage(context) ?: "en")
                            .build()
                        language = SharedPreference.getLanguage(context) ?: "en"
                    },
                    onChangeMode = { theme = it },
                    language = language,
                )
            }
        }
    }
}

fun Context.updateLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    Locale.setDefault(locale)
    config.setLocale(locale)
    return createConfigurationContext(config)
}


