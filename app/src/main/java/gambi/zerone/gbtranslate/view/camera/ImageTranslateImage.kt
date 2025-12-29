package gambi.zerone.gbtranslate.view.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import gambi.zerone.gbtranslate.LoadingScreen
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.textToSpeech
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun ImageTranslateScreen(
    bitmap: Bitmap,
    viewModel: MainViewModel = viewModel(),
    inputLanguage: String = TranslateLanguage.ENGLISH,
    outputLanguage: String = TranslateLanguage.VIETNAMESE,
    toLanguageScreen: (LanguageType) -> Unit,
    onBack: () -> Unit,
    onExchange: () -> Unit,
    localizedContext: Context
) {
    var startDragOffset by remember { mutableStateOf<Offset?>(null) }
    var endDragOffset by remember { mutableStateOf<Offset?>(null) }

    var scale by remember { mutableStateOf(1f) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }

    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var renderInfo by remember { mutableStateOf<ImageRenderInfo?>(null) }

    var cropRectCompose by remember { mutableStateOf<RectF?>(null) }
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var hideHud by remember { mutableStateOf(false) }

    var dialogVisible by remember { mutableStateOf(false) }

    val ocrBoxes = viewModel.ocrBoxes
    val translatedText = viewModel.translatedText

    var imageMode by remember { mutableStateOf(ImageMode.CLIP) }

    val context = LocalContext.current

    LaunchedEffect(imageMode) {
        startDragOffset = null
        endDragOffset = null
    }

    Box() {
        val isLoading by IS_LOADING.collectAsState()

        LaunchedEffect(isLoading) {
            Log.d("isLoading", "isLoading: $isLoading")
        }

        Column(Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {

                // ===== IMAGE =====
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state =
                        rememberTransformableState { zoomChange, panChange, rotationChange ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)
                            val extraWidth = (scale - 1) * constraints.maxWidth
                            val extraHeight = (scale - 1) * constraints.maxHeight

                            val maxX = (extraWidth / 2)
                            val maxY = (extraHeight / 2)

                            imageOffset = Offset(
                                x = (imageOffset.x + panChange.x).coerceIn(-maxX, maxX),
                                y = (imageOffset.y + panChange.y).coerceIn(-maxY, maxY)
                            )
                            imageOffset += (panChange * 1.5f)
                        }

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged {
                                imageSize = it
                                renderInfo = calculateImageRenderInfo(bitmap, it)
                            }
                            .pointerInput(dialogVisible || imageMode != ImageMode.CLIP) {
                                if (dialogVisible || imageMode != ImageMode.CLIP) return@pointerInput

                                if (imageMode == ImageMode.CLIP) {
                                    detectDragGestures(
                                        onDragStart = {
                                            hideHud = true
                                            startDragOffset = it
                                            endDragOffset = it
                                        },
                                        onDrag = { change, _ ->
                                            endDragOffset = change.position
                                        },
                                        onDragEnd = {
                                            hideHud = false
                                        },
                                        onDragCancel = {
                                            hideHud = false
                                        }
                                    )
                                } else {
                                    startDragOffset = null
                                    endDragOffset = null
                                }
                            }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = imageOffset.x
                                translationY = imageOffset.y
                            }
                            .transformable(state = state, enabled = imageMode == ImageMode.ZOOM)

                    )
                }


                // ===== SELECTION RECT (COMPOSE SPACE) =====
                val selectionRect =
                    if (startDragOffset != null && endDragOffset != null) {
                        RectF(
                            min(startDragOffset!!.x, endDragOffset!!.x),
                            min(startDragOffset!!.y, endDragOffset!!.y),
                            max(startDragOffset!!.x, endDragOffset!!.x),
                            max(startDragOffset!!.y, endDragOffset!!.y)
                        )
                    } else null

                cropRectCompose = selectionRect

                // ===== DRAW SELECTION =====
                Canvas(Modifier.fillMaxSize()) {
                    selectionRect?.let {
                        drawRect(
                            color = Color.Red.copy(alpha = 0.1f),
                            topLeft = Offset(it.left, it.top),
                            size = Size(it.width(), it.height())
                        )
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(it.left, it.top),
                            size = Size(it.width(), it.height()),
                            style = Stroke(3f)
                        )
                    }
                }

                // ===== OCR HIGHLIGHT (CHỈ TRONG CROP) =====
                if (dialogVisible && croppedBitmap != null && cropRectCompose != null) {

                    val crop = cropRectCompose!!
                    val cropBmp = croppedBitmap!!

                    Canvas(
                        modifier = Modifier
                            .offset {
                                IntOffset(crop.left.toInt(), crop.top.toInt())
                            }
                            .size(
                                with(LocalDensity.current) {
                                    crop.width().toDp() to crop.height().toDp()
                                }.let { DpSize(it.first, it.second) }
                            )
                    ) {
                        val scaleX = size.width / cropBmp.width
                        val scaleY = size.height / cropBmp.height

                        ocrBoxes.forEach { box ->
                            val r = box.rect

                            drawRoundRect(
                                color = Color.Yellow.copy(alpha = 0.35f),
                                topLeft = Offset(
                                    r.left * scaleX,
                                    r.top * scaleY
                                ),
                                size = Size(
                                    (r.right - r.left) * scaleX,
                                    (r.bottom - r.top) * scaleY
                                ),
                                cornerRadius = CornerRadius(6f)
                            )
                        }
                    }
                }

                // ===== OVERLAY + DIALOG =====
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
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
            AnimatedVisibility(visible = !hideHud) {
                LanguagePicker(
                    toTranslateScreen = toLanguageScreen,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    onExchange = onExchange
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Spacer(Modifier.width(8.dp))
                    AnimatedVisibility(visible = !hideHud) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFCC443),
                                contentColor = Color.Black
                            ),
                            shape = CircleShape,
                            onClick = {
                                imageMode =
                                    if (imageMode == ImageMode.ZOOM) ImageMode.CLIP else ImageMode.ZOOM
                                if (imageMode == ImageMode.ZOOM) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.use_two_fingers_to_zoom_and_move_the_image),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else Toast.makeText(
                                    context,
                                    context.getString(R.string.drag_your_finger_to_select_an_area),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(if (imageMode == ImageMode.ZOOM) R.drawable.ic_zoom else R.drawable.ic_crop),
                                    contentDescription = "Image mode",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    if (imageMode == ImageMode.ZOOM) localizedContext.resources.getString(
                                        R.string.zoom
                                    ) else localizedContext.resources.getString(
                                        R.string.clip
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                AnimatedVisibility(visible = !hideHud) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFCC443),
                            contentColor = Color.Black
                        ),
                        enabled = renderInfo != null && startDragOffset != null && endDragOffset != null,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier,
                        onClick = {
                            IS_LOADING.value = true
                            val info = renderInfo ?: return@Button
                            val start = startDragOffset ?: return@Button
                            val end = endDragOffset ?: return@Button

                            val crop = cropBitmap(
                                bitmap = bitmap,
                                info = info,
                                start = start,
                                end = end,
                                currentScale = scale,
                                currentOffset = imageOffset,
                                containerSize = imageSize // Truyền thêm cái này
                            )

                            croppedBitmap = crop
                            dialogVisible = true
                            viewModel.recognizeAndTranslate(
                                crop,
                                inputLanguage,
                                outputLanguage
                            )
                        }
                    ) {
                        Text(localizedContext.resources.getString(R.string.translate))
                    }
                }
            }
        }

        if (dialogVisible && !isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            dialogVisible = false
                            startDragOffset = null
                            endDragOffset = null
                            cropRectCompose = null
                            croppedBitmap = null
                            viewModel.clear()
                        }
                    }
            )

            TranslationDialog(
                translatedText = translatedText.ifEmpty { localizedContext.resources.getString(R.string.no_text_detected) },
                outputLanguage = outputLanguage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
        if (isLoading) {
            LoadingScreen()
        }
    }
}

@Composable
fun TranslationDialog(
    translatedText: String,
    outputLanguage: String,
    modifier: Modifier = Modifier
) {
    val textToSpeech = remember { mutableStateOf<TextToSpeech?>(null) }
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                outputLanguage.toLanguageDisplayName(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2962FF)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = translatedText,
                modifier = Modifier
                    .height(240.dp)
                    .verticalScroll(rememberScrollState())
            )
            Spacer(Modifier.height(8.dp))

            Icon(
                painter = painterResource(R.drawable.ic_speaker),
                contentDescription = "Speaker",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable {
                    textToSpeech(
                        textToSpeech = textToSpeech,
                        context = context,
                        language = outputLanguage,
                        text = translatedText
                    )
                }
            )
        }
    }
}

data class ImageRenderInfo(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float
)

fun calculateImageRenderInfo(
    bitmap: Bitmap,
    container: IntSize
): ImageRenderInfo {

    val scale = min(
        container.width / bitmap.width.toFloat(),
        container.height / bitmap.height.toFloat()
    )

    val drawWidth = bitmap.width * scale
    val drawHeight = bitmap.height * scale

    val offsetX = (container.width - drawWidth) / 2f
    val offsetY = (container.height - drawHeight) / 2f

    return ImageRenderInfo(scale, offsetX, offsetY)
}

fun cropBitmap(
    bitmap: Bitmap,
    info: ImageRenderInfo,
    start: Offset,
    end: Offset,
    currentScale: Float,
    currentOffset: Offset,
    containerSize: IntSize // Cần thêm kích thước khung chứa
): Bitmap {
    val minX = min(start.x, end.x)
    val minY = min(start.y, end.y)
    val width = abs(start.x - end.x)
    val height = abs(start.y - end.y)

    // Tạo một Ma trận để đảo ngược các phép biến đổi
    val matrix = android.graphics.Matrix()

    // 1. Phép biến đổi mặc định (Fit vào màn hình)
    matrix.postScale(info.scale, info.scale)
    matrix.postTranslate(info.offsetX, info.offsetY)

    // 2. Phép biến đổi do người dùng Zoom/Pan
    // Lưu ý: graphicsLayer mặc định zoom từ tâm View
    matrix.postScale(
        currentScale, currentScale,
        containerSize.width / 2f,
        containerSize.height / 2f
    )
    matrix.postTranslate(currentOffset.x, currentOffset.y)

    // 3. Đảo ngược ma trận để chuyển tọa độ màn hình về tọa độ Bitmap gốc
    val inverseMatrix = android.graphics.Matrix()
    matrix.invert(inverseMatrix)

    val points = floatArrayOf(minX, minY, minX + width, minY + height)
    inverseMatrix.mapPoints(points)

    val left = points[0].toInt().coerceIn(0, bitmap.width)
    val top = points[1].toInt().coerceIn(0, bitmap.height)
    val right = points[2].toInt().coerceIn(0, bitmap.width)
    val bottom = points[3].toInt().coerceIn(0, bitmap.height)

    val finalWidth = (right - left).coerceAtLeast(1)
    val finalHeight = (bottom - top).coerceAtLeast(1)

    return Bitmap.createBitmap(bitmap, left, top, finalWidth, finalHeight)
}

var IS_LOADING = MutableStateFlow(false)

