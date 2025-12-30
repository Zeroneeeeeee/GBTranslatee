package gambi.zerone.gbtranslate.view.component

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.view.translatehistory.NotifyDialog
import androidx.core.content.edit

@Composable
fun VoiceAccessDialog(
    onGranted: () -> Unit = {},
    onDismiss: () -> Unit = {},
    localizedContext: Context
) {
    PermissionDialog(
        permission = Manifest.permission.RECORD_AUDIO,
        iconRes = R.drawable.ic_mic_outline,
        title = localizedContext.getString(R.string.voice_access),
        description = localizedContext.getString(R.string.please_allow_recording_mode_to_operate),
        prefsKey = KEY_MIC_DENIED,
        onGranted = onGranted,
        onDismiss = onDismiss
    )
}

@Composable
fun CameraAccessDialog(
    onGranted: () -> Unit = {},
    onDismiss: () -> Unit = {},
    localizedContext: Context
) {
    PermissionDialog(
        permission = Manifest.permission.CAMERA,
        iconRes = R.drawable.ic_camera,
        title = localizedContext.getString(R.string.camera_access),
        description = localizedContext.getString(R.string.do_you_want_to_translate_text_by_taking_a_photo),
        prefsKey = KEY_CAMERA_DENIED,
        onGranted = onGranted,
        onDismiss = onDismiss
    )
}

@Composable
fun PermissionDialog(
    permission: String,
    iconRes: Int,
    title: String,
    description: String,
    prefsKey: String,
    onGranted: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    val deniedBefore = remember { mutableStateOf(prefs.getBoolean(prefsKey, false)) }

    // Launcher xin quyền
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            prefs.edit { putBoolean(prefsKey, false) }
            deniedBefore.value = false
            onDismiss()
            onGranted()
        } else {
            prefs.edit { putBoolean(prefsKey, true) }
            deniedBefore.value = true
            onDismiss()
        }
    }

    // Launcher mở Settings
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            prefs.edit { putBoolean(prefsKey, false) }
            deniedBefore.value = false
            onGranted()
            onDismiss()
        }
    }

    NotifyDialog(
        icon = iconRes,
        title = title,
        description = description,
        confirmText = context.getString(R.string.turn_on),
        cancelText = context.getString(R.string.maybe_later),
        onConfirm = {
            when {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
                }
                deniedBefore.value -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .apply { data = Uri.fromParts("package", context.packageName, null) }
                    settingsLauncher.launch(intent)
                }
                else -> {
                    permissionLauncher.launch(permission)
                }
            }
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}


fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private const val PREF_NAME = "permission_prefs"
private const val KEY_CAMERA_DENIED = "camera_denied"
private const val KEY_MIC_DENIED = "mic_denied"