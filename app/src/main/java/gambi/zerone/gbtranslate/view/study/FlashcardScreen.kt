package gambi.zerone.gbtranslate.view.study

// FlashcardScreen.kt
// Jetpack Compose flashcard with REAL swipe + flip animation
// FlashcardScreen.kt
// COMPLETE & CLEAN Flashcard implementation (Flip + Swipe + Stack)

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
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
    onBack: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    var knownCount by remember { mutableIntStateOf(0) }
    var unknownCount by remember { mutableIntStateOf(0) }
    var cardList by remember { mutableStateOf(cards) }

    val unknownCards = remember{mutableStateListOf<FlashCardVM>()}

    var dragX by remember { mutableFloatStateOf(0f) }

    val leftProgress = (-dragX / 100f).coerceIn(0f, 1f)
    val rightProgress = (dragX / 100f).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(title = localizedContext.resources.getString(R.string.flashcards), onBack = onBack)

        if (index >= cardList.size) {
            ResultScreen(
                progress = "${knownCount / cardList.size.toFloat() * 100}%",
                currentProgress = knownCount / cardList.size.toFloat(),
                known = knownCount,
                unknown = unknownCount,
                total = cardList.size,
                onRestart = {
                    cardList = cards.toList()
                    index = 0
                    knownCount = 0
                    unknownCount = 0
                },
                onPracticeAgain = {
                    cardList = unknownCards.toList()
                    index = 0
                    knownCount = 0
                    unknownCount = 0
                    unknownCards.clear()
                },
                modifier = Modifier.padding(16.dp)
            )
        } else {
            ProgressBar(
                currentProgress = index / cardList.size.toFloat(),
                progress = "$index/${cardList.size}",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(Modifier.weight(0.5f))
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                for (i in 2 downTo 0) {
                    val cardIndex = index + i
                    if (cardIndex < cardList.size) {
                        FlashCardItem(
                            card = cardList[cardIndex],
                            isTop = i == 0,
                            scale = 1f - i * 0.05f,
                            offsetY = (i * 12).dp,
                            onSwipedLeft = {
                                unknownCount++
                                unknownCards.add(cardList[index])
                                index++
                            },
                            getCardState = { top, drag ->
                                dragX = drag
                            },
                            onSwipedRight = {
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
                SwipeRevealAction(
                    progress = leftProgress,
                    text = "Learn",
                    icon = R.drawable.ic_practice_again,
                    background = Color(0xFFFFC83D),
                    alignStart = true,
                )
                Spacer(Modifier.weight(1f))

                SwipeRevealAction(
                    progress = rightProgress,
                    text = "Known",
                    icon = R.drawable.ic_studied,
                    background = Color(0xFF3162FF),
                    alignStart = false,
                )

            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    currentProgress: Float = 0.5f,
    progress: String = "",
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
            text = progress,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF3162FF)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    progress: String = "50%",
    currentProgress: Float = 0.5f,
    known: Int = 0,
    unknown: Int = 0,
    total: Int = 0,
    onRestart: () -> Unit = {},
    onPracticeAgain: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        Image(
            painter = painterResource(R.drawable.img_congratulation),
            contentDescription = "Congratulation",
        )
        Spacer(Modifier.height(40.dp))
        ProgressBar(currentProgress = currentProgress, progress = progress)
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Learned",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(Color(0xFFE8F0F7), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$known/$total",
                    color = Color(0xFF3162FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_studied),
                    contentDescription = "Learned Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Practice again",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(Color(0xFFE8F0F7), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$unknown/$total",
                    color = Color(0xFFFBB004),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_practice_again),
                    contentDescription = "Learned Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(onClick = onRestart, modifier = Modifier.weight(1f)) {
                Text("Restart practice")
            }
            if (unknown > 0) {
                Spacer(Modifier.width(16.dp))
                Button(onClick = onPracticeAgain, modifier = Modifier.weight(1f)) {
                    Text("Practice $unknown cards")
                }
            }
        }
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
    getCardState: (Boolean, Float) -> Unit
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

    LaunchedEffect(dragX) {
        getCardState(isTop, dragX)
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
        dragX > 0 -> Color(0xFFBEE9FF) // Vuốt phải → xanh
        dragX < 0 -> Color(0xFFFFEDB4) // Vuốt trái → đỏ
        else -> Color.Transparent
    }
    val borderColor = when {
        dragX > 0 -> Color(0xFF5C83FF) // Vuốt phải → xanh
        dragX < 0 -> Color(0xFFFCCD5E) // Vuốt trái → đỏ
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
            .background(Color(0xFFECF3FE))
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
        if (isTop && overlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        overlayColor.copy(alpha = overlayAlpha)
                    )
                    .border(2.dp, borderColor.copy(alpha = overlayAlpha), RoundedCornerShape(20.dp))
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_speaker),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .clickable {
                    textToSpeech(
                        textToSpeech = textToSpeech,
                        context = context,
                        language = TranslateLanguage.ENGLISH,
                        text = if (flipRotation <= 90f) card.front else card.back
                    )
                }
                .align(if (flipRotation <= 90) Alignment.TopStart else Alignment.TopEnd)
                .graphicsLayer { rotationY = if (flipRotation <= 90) 0f else 180f }
                .padding(16.dp)
        )

        // ===== TEXT =====
        if (flipRotation <= 90f) {
            Text(
                text = card.front,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Text(
                text = card.back,
                fontSize = 28.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer { rotationY = 180f }
            )
        }

    }
}

@Composable
private fun SwipeRevealAction(
    modifier: Modifier = Modifier,
    progress: Float,
    text: String,
    icon: Int,
    background: Color,
    alignStart: Boolean
) {
    val minWidth = 44.dp
    val maxWidth = 120.dp
    val width = lerp(minWidth, maxWidth, progress)

    Row(
        modifier = modifier
            .padding(12.dp)
            .width(width)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!alignStart) Spacer(Modifier.weight(1f))
        if (!alignStart && progress > 0.25f) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Spacer(Modifier.width(8.dp))
        }

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )

        if (alignStart && progress > 0.25f) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}



