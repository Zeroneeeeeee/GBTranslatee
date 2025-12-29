package gambi.zerone.gbtranslate.view.home

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.gbtranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import gambi.zerone.gbtranslate.CameraAccessDialog
import gambi.zerone.gbtranslate.VoiceAccessDialog
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.VoiceToTextParser
import gambi.zerone.gbtranslate.view.home.component.MainHeader
import gambi.zerone.gbtranslate.view.home.component.TranslateModes
import gambi.zerone.gbtranslate.view.home.component.Translator

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    toLanguageScreen: (LanguageType) -> Unit = {},
    toTextTranslate: (String,String) -> Unit,
    inputLanguage: String,
    outputLanguage: String,
    application: Application,
    localizedContext: Context,
    onExchange: () -> Unit,
    toCameraScreen: () -> Unit,
    toConversationScreen: () -> Unit,
    toSettingScreen: () -> Unit,
    toStudyScreen: () -> Unit = {},
    toHistoryScreen: () -> Unit = {}
) {
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }

    // ================= STATE =================
    var showSpeechDialog by remember { mutableStateOf(false) }
    var showMicPermissionDialog by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by remember { mutableStateOf(false) }

    /* ================= PERMISSION ================= */

    val micPermission = Manifest.permission.RECORD_AUDIO
    val cameraPermission = Manifest.permission.CAMERA

    /* ================= HANDLERS ================= */

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

    /* ================= UI ================= */

    Content(
        modifier = modifier
            .background(Color.Transparent)
            .padding(16.dp),
        toChoosingLanguage = toLanguageScreen,
        toTextTranslate = toTextTranslate,
        inputLanguage = inputLanguage,
        outputLanguage = outputLanguage,
        onExchange = onExchange,
        localizedContext = localizedContext,
        onVoiceToText = {
            handleVoiceClick()
        },
        inputText = inputText,
        toCameraScreen = {
            handleCameraClick()
        },
        toConversationScreen = toConversationScreen,
        toSettingScreen = toSettingScreen,
        toStudyScreen = toStudyScreen,
        toHistoryScreen = toHistoryScreen
    )

    /* ================= CUSTOM DIALOG ================= */

    if (showMicPermissionDialog) {
        VoiceAccessDialog(
            localizedContext = localizedContext,
            onGranted = {
                Log.d("TAG", "onGranted: ")
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

    /* ================= SPEECH ================= */

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
            },
            localizedContext = localizedContext
        )
    }
}


@Composable
private fun Content(
    modifier: Modifier = Modifier,
    toChoosingLanguage: (LanguageType) -> Unit = {},
    toTextTranslate: (String, String) -> Unit,
    inputLanguage: String,
    outputLanguage: String,
    inputText: String,
    localizedContext: Context,
    onExchange: () -> Unit,
    onVoiceToText: () -> Unit,
    toCameraScreen: () -> Unit,
    toConversationScreen: () -> Unit,
    toSettingScreen: () -> Unit = {},
    toStudyScreen: () -> Unit = {},
    toHistoryScreen: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        MainHeader(onSettingClick = toSettingScreen, toHistoryScreen = toHistoryScreen)
        Spacer(modifier = Modifier.height(16.dp))
        Translator(
            inputText = inputText,
            toTextTranslate = toTextTranslate,
            toChoosingLanguage = toChoosingLanguage,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage,
            onExchange = onExchange,
            onVoiceToText = onVoiceToText,
            toCameraScreen = toCameraScreen,
            localizedContext = localizedContext

        )
        Spacer(modifier = Modifier.height(16.dp))
        TranslateModes(
            toConversationScreen = toConversationScreen,
            toCameraScreen = toCameraScreen,
            toShowVoiceDialog = onVoiceToText,
            toStudyScreen = toStudyScreen,
            localizedContext = localizedContext
        )
    }
}

@Composable
fun SpeechDialog(
    modifier: Modifier = Modifier,
    language: String = TranslateLanguage.ENGLISH,
    application: Application = LocalContext.current.applicationContext as Application,
    buttonColor: Color = Color(0xFF3162FF),
    fadeColor: Color = Color(0xFFB8C6FF),
    onDismiss: () -> Unit = {},
    onTextReceived: (String) -> Unit = {},
    localizedContext: Context
) {
    val voiceToTextParser = remember {
        VoiceToTextParser(application)
    }

    var isRecording by remember {
        mutableStateOf(false)
    }

    var canRecord by remember {
        mutableStateOf(false)
    }

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            canRecord = isGranted
        }
    )

    val state by voiceToTextParser.state.collectAsState()

    LaunchedEffect(recordAudioLauncher) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(state.spokenText) {
        if (state.spokenText.isNotEmpty() && isRecording) {
            onTextReceived(state.spokenText)
            onDismiss()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f))
            .clickable(
                onClick = {
                    voiceToTextParser.stopListening()
                    onDismiss()
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = modifier
                .width(300.dp)
                .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                .padding(16.dp)
                .clickable {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close",
                tint = Color(0xFFBDBDBD),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        voiceToTextParser.stopListening()
                        onDismiss()
                    }
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = localizedContext.resources.getString(R.string.voice),
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (state.isSpeaking) localizedContext.resources.getString(R.string.listening) else localizedContext.resources.getString(
                    R.string.tap_on_the_voice_button_to_start
                ),
                color = Color(0xFF9EA5AE)
            )
            Spacer(Modifier.height(24.dp))
            MicPulseButton(
                topColor = buttonColor,
                bottomColor = fadeColor,
                isSpeaking = state.isSpeaking,
                onClick = {
                    // logic cũ của bạn, giữ nguyên
                    if (state.isSpeaking) {
                        voiceToTextParser.stopListening()
                    } else {
                        voiceToTextParser.startListening(language)
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    voiceToTextParser.stopListening()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color.Black.copy(alpha = 0.5f),
                        spotColor = Color.LightGray.copy(alpha = 0.01f)
                    )
            ) {
                Text(
                    text = localizedContext.resources.getString(R.string.cancel),
                    fontSize = 16.sp,
                    color = Color(0xFFCBCDD3)
                )
            }
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun MicPulseButton(
    modifier: Modifier = Modifier,
    isSpeaking: Boolean = false,
    onClick: () -> Unit = {},
    topColor: Color = Color(0xFFED6490),
    bottomColor: Color = Color(0xFFFF9BA6),
    iconTint: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearOutSlowInEasing)
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300)
        ),
        label = "alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        // 🌫 Outer soft glow
        if (isSpeaking) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .background(
                        topColor.copy(alpha = alpha),
                        CircleShape
                    )
            )
        }

        // 🔵 Main mic button (LINEAR gradient)
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(topColor, bottomColor),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    ),
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_mic_fill),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
