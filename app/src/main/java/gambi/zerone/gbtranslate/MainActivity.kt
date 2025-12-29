package gambi.zerone.gbtranslate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.gbtranslate.R
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
                darkTheme = when(theme) {
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
//                CameraScreen()
                //PreviewScreen()
                //AddFlashcard()
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


