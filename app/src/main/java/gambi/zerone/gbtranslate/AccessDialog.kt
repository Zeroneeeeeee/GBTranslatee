package gambi.zerone.gbtranslate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.view.home.openAppSettings
import gambi.zerone.gbtranslate.view.translatehistory.NotifyDialog

@Composable
fun VoiceAccessDialog(
    modifier: Modifier = Modifier,
    onGranted: () -> Unit = {},
    onDismiss: () -> Unit = {},
    localizedContext: Context
) {
    val context = LocalContext.current

    val prefs = remember {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    val micPermission = Manifest.permission.RECORD_AUDIO
    val micDeniedBefore = remember {
        mutableStateOf(prefs.getBoolean(KEY_MIC_DENIED, false))
    }

    val micLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            prefs.edit().putBoolean(KEY_MIC_DENIED, false).apply()
            micDeniedBefore.value = false
            onDismiss()
            onGranted()
            //showSpeechDialog = true
        } else {
            prefs.edit().putBoolean(KEY_MIC_DENIED, true).apply()
            micDeniedBefore.value = true
            onDismiss()
        }
    }

    NotifyDialog(
        icon = R.drawable.ic_mic_outline,
        title = localizedContext.resources.getString(R.string.voice_access),
        description = localizedContext.resources.getString(R.string.please_allow_recording_mode_to_operate),
        confirmText = localizedContext.resources.getString(R.string.turn_on),
        cancelText = localizedContext.resources.getString(R.string.maybe_later),
        onConfirm = {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    micPermission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
//                    showSpeechDialog = true
                }

                micDeniedBefore.value -> {
                    openAppSettings(context)
                }

                else -> {
                    micLauncher.launch(micPermission)
                }
            }

                    },
        onDismiss = onDismiss
    )
}

@Composable
fun CameraAccessDialog(
    modifier: Modifier = Modifier,
    onGranted: () -> Unit = {},
    onDismiss: () -> Unit = {},
    localizedContext: Context
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    val cameraPermission = Manifest.permission.CAMERA

    val cameraDeniedBefore = remember {
        mutableStateOf(prefs.getBoolean(KEY_CAMERA_DENIED, false))
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            prefs.edit().putBoolean(KEY_CAMERA_DENIED, false).apply()
            cameraDeniedBefore.value = false
            onDismiss()
            onGranted()
        } else {
            prefs.edit().putBoolean(KEY_CAMERA_DENIED, true).apply()
            cameraDeniedBefore.value = true
            onDismiss()
        }
    }
    NotifyDialog(
        icon = R.drawable.ic_camera,
        title = localizedContext.resources.getString(R.string.camera_access),
        description = localizedContext.resources.getString(R.string.do_you_want_to_translate_text_by_taking_a_photo),
        confirmText = localizedContext.resources.getString(R.string.turn_on),
        cancelText = localizedContext.resources.getString(R.string.maybe_later),
        onConfirm = {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    cameraPermission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
                }

                cameraDeniedBefore.value -> {
                    openAppSettings(context)
                }

                else -> {
                    cameraLauncher.launch(cameraPermission)
                }
            }
        },
        onDismiss = onDismiss
    )
}

private const val PREF_NAME = "permission_prefs"
private const val KEY_CAMERA_DENIED = "camera_denied"
private const val KEY_MIC_DENIED = "mic_denied"