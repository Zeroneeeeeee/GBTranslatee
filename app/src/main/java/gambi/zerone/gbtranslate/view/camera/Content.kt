package gambi.zerone.gbtranslate.view.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    applicationContext: Context,
    inputLanguage: String,
    outputLanguage: String,
    toTranslateScreen: (LanguageType) -> Unit = {},
    toImageTranslate: (Bitmap) -> Unit = {},
    onBack: () -> Unit,
    onExchange: () -> Unit = {},
) {
    rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember {
        LifecycleCameraController(applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }
    val viewModel = viewModel<MainViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()

    val context = applicationContext

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }

            toImageTranslate(bitmap)
        }
    }

//NOTE
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheetContent(
                bitmaps = bitmaps,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
            )

            Column {
                CameraHeader(
                    onBack = onBack,
                    modifier = Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
                )
                LanguagePicker(
                    toTranslateScreen = toTranslateScreen,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    onExchange = onExchange,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f),
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_broken_image_24),
                        contentDescription = "Open gallery",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            onPhotoTaken = {
                                viewModel::onTakePhoto
                                toImageTranslate(it)
                            },
                            applicationContext = applicationContext
                        )
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, CircleShape)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_translate),
                        contentDescription = "Take photo",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

private fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    applicationContext: Context
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LanguagePicker(
    modifier: Modifier = Modifier,
    inputLanguage: String = "Language",
    outputLanguage: String = "Language",
    toTranslateScreen: (LanguageType) -> Unit = {},
    onExchange: () -> Unit = {}
) {
    LocalContext.current
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(100.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFF3C393C), RoundedCornerShape(100.dp))
                .padding(16.dp)
                .weight(1f)
                .clickable {
                    toTranslateScreen(LanguageType.INPUT)
                }
        ) {
            Text(
                text = inputLanguage.toLanguageDisplayName(),
                color = Color.White,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )
            Icon(
                painter = painterResource(R.drawable.ic_down_navigate),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = onExchange
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_exchange),
                contentDescription = "Change Language",
                tint = Color.White
            )
        }
        Row(
            modifier = Modifier
                .background(Color(0xFF3C393C), RoundedCornerShape(100.dp))
                .padding(16.dp)
                .weight(1f)
                .clickable {
                    toTranslateScreen(LanguageType.OUTPUT)
                }
        ) {
            Text(
                text = outputLanguage.toLanguageDisplayName(),
                color = Color.White,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )
            Icon(
                painter = painterResource(R.drawable.ic_down_navigate),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CameraHeader(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_left_navigate),
            contentDescription = "Back Icon",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onBack()
                }
        )
    }
}