package gambi.zerone.gbtranslate

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbtranslate.R

@Composable
fun OnBoardingScreen(modifier: Modifier = Modifier, localizeContext: Context, toHome: () -> Unit = {}) {
    var position by remember { mutableStateOf(0) }
    val pages = listOf(
        OnBoardingPage(
            R.drawable.img_onboarding1,
            localizeContext.resources.getString(R.string.translate_all_languages),
            localizeContext.resources.getString(R.string.translate_text_quickly_and_accurately_across_hundreds_of_languages)
        ),
        OnBoardingPage(
            R.drawable.img_onboarding2,
            localizeContext.resources.getString(R.string.image_translation),
            localizeContext.resources.getString(R.string.understand_conversations_and_texts_instantly_just_speak_or_scan_to_translate)
        ),
        OnBoardingPage(
            R.drawable.img_onboarding3,
            localizeContext.resources.getString(R.string.translate_the_conversation),
            localizeContext.resources.getString(R.string.break_language_barriers_communicate_with_confidence_anytime_anywhere)
        ),
        OnBoardingPage(
            R.drawable.img_onboarding4,
            localizeContext.resources.getString(R.string.boost_vocabulary_with_flashcards),
            localizeContext.resources.getString(R.string.flashcards_help_you_remember_new_words_longer_with_images_and_examples)
        )
    )

    Content(
        onBoardingPage = pages[position],
        position = position,
        localizeContext = localizeContext,
        onNext = {
            if (position < pages.size - 1) {
                position++
            } else {
                toHome()
            }
        },
        onSkip = toHome
    )

}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    position: Int = 0,
    localizeContext: Context,
    onBoardingPage: OnBoardingPage = OnBoardingPage(),
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 28.dp)
    ) {
        Body(
            image = onBoardingPage.image,
            title = onBoardingPage.title,
            description = onBoardingPage.description,
            localizeContext = localizeContext,
            onSkip = onSkip
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp),
        ) {
            PageProgressIndicator(
                currentPage = position,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onNext,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF3162FF)
                ),
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    tint = Color.White,
                    contentDescription = "Arrow Right",
                    modifier = Modifier.padding()

                )
            }
        }
    }
}

@Composable
fun Body(
    modifier: Modifier = Modifier,
    image: Int = R.drawable.img_onboarding1,
    title: String = "Title",
    description: String = "Description",
    localizeContext: Context,
    onSkip: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = localizeContext.resources.getString(R.string.skip),
            color = Color(0xFF9EA5AE),
            lineHeight = 46.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onSkip() }
        )
        Spacer(modifier = Modifier.weight(0.15f))
        Image(
            painter = painterResource(image),
            contentDescription = "OnBoarding",
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,

            color = Color(0xFF9EA5AE)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun PageProgressIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPage: Int = 4,
) {
    BoxWithConstraints(
        modifier = modifier
            .width(60.dp)
            .height(7.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE5E7EB)) // track
    ) {
        val trackWidth = maxWidth
        val indicatorWidth = trackWidth / totalPage
        val maxOffset = trackWidth - indicatorWidth

        val progress = (currentPage).toFloat() / (totalPage - 1).coerceAtLeast(1)

        val offsetX by animateDpAsState(
            targetValue = maxOffset * progress,
            animationSpec = tween(300),
            label = ""
        )

        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(indicatorWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF4D7CFE)) // indicator xanh
        )
    }
}

data class OnBoardingPage(
    val image: Int = R.drawable.img_onboarding1,
    val title: String = "Title",
    val description: String = "Description"
)
