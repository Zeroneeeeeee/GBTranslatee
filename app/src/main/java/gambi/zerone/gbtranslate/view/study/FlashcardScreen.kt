package gambi.zerone.gbtranslate.view.study

// FlashcardScreen.kt
// Jetpack Compose flashcard with REAL swipe + flip animation
// FlashcardScreen.kt
// COMPLETE & CLEAN Flashcard implementation (Flip + Swipe + Stack)

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbtranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import gambi.zerone.gbtranslate.utils.textToSpeech
import gambi.zerone.gbtranslate.view.conversation.Header
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun FlashcardScreen(
    cards: List<FlashCardVM>,
    localizedContext: Context,
    onKnown: (FlashCardVM) -> Unit = {},
    onUnknown: (FlashCardVM) -> Unit = {},
    onBack: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    var knownCount by remember { mutableIntStateOf(0) }
    var unknownCount by remember { mutableIntStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(title = localizedContext.resources.getString(R.string.flashcards), onBack = onBack)
        ProgressBar(
            currentProgress = index / cards.size.toFloat(),
            progress = index,
            total = cards.size,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(Modifier.weight(0.5f))
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            if (index >= cards.size) {
                Text(
                    localizedContext.resources.getString(R.string.complete),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                return@Box
            }

            // Render stack (bottom -> top)
            for (i in 2 downTo 0) {
                val cardIndex = index + i
                if (cardIndex < cards.size) {
                    FlashCardItem(
                        card = cards[cardIndex],
                        isTop = i == 0,
                        scale = 1f - i * 0.05f,
                        offsetY = (i * 12).dp,
                        onSwipedLeft = {
                            onUnknown(cards[index])
                            unknownCount++
                            index++
                        },
                        onSwipedRight = {
                            onKnown(cards[index])
                            knownCount++
                            index++
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = localizedContext.resources.getString(R.string.unknown, unknownCount),
                color = Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = localizedContext.resources.getString(R.string.known, knownCount),
                color = Color.Green,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    currentProgress: Float = 0.5f,
    progress: Int = 0,
    total: Int = 10
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator(
            progress = { currentProgress },
            color = Color(0xFF3162FF),
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            gapSize = 0.dp,
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {},
            modifier = Modifier
                .height(16.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$progress/$total",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun FlashCardItem(
    modifier: Modifier = Modifier,
    card: FlashCardVM,
    isTop: Boolean,
    scale: Float,
    offsetY: Dp,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
) {
    var flipped by remember { mutableStateOf(false) }
    var dragX by remember { mutableFloatStateOf(0f) }


    val context = LocalContext.current
    val textToSpeech = remember { mutableStateOf<TextToSpeech?>(null) }

    // RESET khi đổi card
    LaunchedEffect(card) {
        dragX = 0f
        flipped = false
    }

    val animatedX by animateFloatAsState(
        targetValue = dragX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "drag"
    )

    val flipRotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(400),
        label = "flip"
    )

    val swipeRotation = animatedX / 35f

    // ===== MÀU SÁNG THEO HƯỚNG VUỐT =====
    val overlayColor = when {
        dragX > 0 -> Color(0xFF4CAF50) // Vuốt phải → xanh
        dragX < 0 -> Color(0xFFF44336) // Vuốt trái → đỏ
        else -> Color.Transparent
    }

    val overlayAlpha =
        (abs(dragX) / 300f).coerceIn(0f, 0.6f)

    Box(
        modifier = modifier
            .offset { IntOffset(animatedX.roundToInt(), 0) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = offsetY.toPx()
                rotationZ = swipeRotation
                rotationY = flipRotation
                cameraDistance = 12 * density
            }
            .size(width = 300.dp, height = 400.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .then(
                if (isTop) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val direction = if (flipRotation <= 90f) 1 else -1
                                dragX += dragAmount.x * direction
                            },
                            onDragEnd = {
                                when {
                                    dragX > 300 -> onSwipedRight()
                                    dragX < -300 -> onSwipedLeft()
                                    else -> dragX = 0f
                                }
                            }
                        )
                    }
                } else Modifier
            )
            .pointerInput(isTop) {
                if (isTop) {
                    detectTapGestures { flipped = !flipped }
                }
            }
    ) {

        // ===== LỚP SÁNG (OVERLAY) =====
        if (isTop && overlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        overlayColor.copy(alpha = overlayAlpha)
                    )
            )
        }

        // ===== ICON =====
        Icon(
            painter = painterResource(R.drawable.ic_speaker),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .clickable {
                    textToSpeech(
                        textToSpeech = textToSpeech,
                        context = context,
                        language = TranslateLanguage.ENGLISH,
                        text = if (flipRotation <= 90f) card.front else card.back
                    )
                }
                .align(if(flipRotation<=90) Alignment.TopStart else Alignment.TopEnd)
                .graphicsLayer { rotationY = if(flipRotation<=90) 0f else 180f }
                .padding(16.dp)
        )

        // ===== TEXT =====
        if (flipRotation <= 90f) {
            Text(
                text = card.front,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Text(
                text = card.back,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer { rotationY = 180f }
            )
        }
    }
}


// ==================== USAGE ====================

/*
FlashcardScreen(
    cards = listOf(
        FlashCard("Hello", "Xin chào"),
        FlashCard("Dog", "Con chó"),
        FlashCard("Cat", "Con mèo")
    ),
    onKnown = { println("Known: ${it.front}") },
    onUnknown = { println("Unknown: ${it.front}") }
)
*/
//@Preview(showBackground = true)
//@Composable
//fun PreviewScreen(modifier: Modifier = Modifier) {
//    FlashcardScreen(
//        cards = listOf(
//            FlashCardVM("Hello", "Xin chào"),
//            FlashCardVM("Dog", "Con chó"),
//            FlashCardVM("Cat", "Con mèo")
//        ),
//        onKnown = { println("Known: ${it.front}") },
//        onUnknown = { println("Unknown: ${it.front}") }
//    )
//}



