package gambi.zerone.gbtranslate.view.texttranslator

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.view.component.CameraAccessDialog
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.view.component.VoiceAccessDialog
import gambi.zerone.gbtranslate.view.conversation.Header
import gambi.zerone.gbtranslate.view.home.SpeechDialog
import gambi.zerone.gbtranslate.view.texttranslator.component.Translator

@Composable
fun TextTranslatorScreen(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    input: String,
    output: String,
    inputLanguage: String,
    outputLanguage: String,
    application: Application,
    toChoosingLanguage: (LanguageType) -> Unit,
    onExchange: () -> Unit,
    toHistoryScreen: () -> Unit,
    toCameraScreen: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        Log.d("TranslateTextField3", "output: $output")
    }
    val context = LocalContext.current

    val micPermission = Manifest.permission.RECORD_AUDIO
    val cameraPermission = Manifest.permission.CAMERA

    var showSpeechDialog by remember {
        mutableStateOf(false)
    }
    var showMicPermissionDialog by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by remember { mutableStateOf(false) }

    var inputText by remember {
        mutableStateOf(input)
    }

    fun handleVoiceClick() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                micPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                showSpeechDialog = true
            }

            else -> {
                showMicPermissionDialog = true
            }
        }
    }

    fun handleCameraClick() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                cameraPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                toCameraScreen()
            }

            else -> {
                showCameraPermissionDialog = true
            }
        }
    }

    Content(
        modifier = modifier,
        localizedContext = localizedContext,
        input = inputText,
        output =output,
        inputLanguage = inputLanguage,
        outputLanguage = outputLanguage,
        toChoosingLanguage = toChoosingLanguage,
        onExchange = onExchange,
        onVoiceToText = {
            handleVoiceClick()
        },
        onCamera = {
            handleCameraClick()
        },
        toHistoryScreen = toHistoryScreen,
        onBack = onBack
    )
    if (showMicPermissionDialog) {
        VoiceAccessDialog(
            localizedContext = localizedContext,
            onGranted = {
                showSpeechDialog = true
            },
            onDismiss = {
                showMicPermissionDialog = false
            }
        )
    }

    if (showCameraPermissionDialog) {
        CameraAccessDialog(
            localizedContext = localizedContext,
            onGranted = {
                toCameraScreen()
            },
            onDismiss = {
                showCameraPermissionDialog = false
            }
        )
    }

    if (showSpeechDialog) {
        SpeechDialog(
            language = inputLanguage,
            application = application,
            buttonColor = Color(0xFF3162FF),
            fadeColor = Color(0xFF7B97F7),
            onDismiss = {
                showSpeechDialog = false
            },
            onTextReceived = {
                inputText = it
                Log.d("SpeechDialog", "onTextReceived: $inputText")
            },
            localizedContext = localizedContext
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    input: String,
    output: String,
    inputLanguage: String,
    outputLanguage: String,
    toChoosingLanguage: (LanguageType) -> Unit,
    onExchange: () -> Unit,
    onVoiceToText: () -> Unit,
    onCamera: () -> Unit,
    toHistoryScreen: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        Log.d("TranslateTextField4", "output: $output")
    }
    Column(modifier = modifier.fillMaxSize()) {
        Header(
            title = localizedContext.resources.getString(R.string.translate),
            onBack = onBack,
            trailing = {
            Icon(
                painter = painterResource(R.drawable.ic_history),
                contentDescription = "History Icon",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp).clickable{toHistoryScreen()},
            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Icon(
//                painter = painterResource(R.drawable.ic_bookmark),
//                contentDescription = "History Icon",
//                tint = Color.Unspecified,
//                modifier = Modifier.size(32.dp),
//            )
        })
        Translator(
            localizedContext = localizedContext,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage,
            input = input,
            output = output,
            toChoosingLanguage = toChoosingLanguage,
            onExchange = onExchange,
            onVoiceToText = onVoiceToText,
            onCamera = onCamera,
            modifier = Modifier.padding(16.dp)
        )
    }
}

