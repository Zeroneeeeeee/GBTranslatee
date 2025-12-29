package gambi.zerone.gbtranslate.view.conversation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import gambi.zerone.gbtranslate.utils.LanguagesUtils

@Composable
fun ConversationBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    buttonColor: Long = 0xFF62FF57,
    onClick: () -> Unit = {},
    conversation: List<Message> = messageSamples,
    inputLanguage: String = TranslateLanguage.ENGLISH,
    outputLanguage: String = TranslateLanguage.VIETNAMESE,
    localizedContext: Context
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Conversations(
            modifier = Modifier.padding(top = 16.dp),
            conversation = conversation,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (conversation.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = localizedContext.resources.getString(R.string.tap_on_the_voice_button_to_start),
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Color(buttonColor),
                            CircleShape
                        )
                        .clickable {
                            onClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic_fill),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Conversations(
    modifier: Modifier = Modifier,
    conversation: List<Message> = messageSamples,
    inputLanguage: String,
    outputLanguage: String
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(items = conversation, key = { it.timestamp }) {
            ConversationChat(
                text = it.text,
                sender = it.sender,
                inputLanguage = inputLanguage,
                outputLanguage = outputLanguage
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun ConversationChat(
    modifier: Modifier = Modifier,
    sender: Int = 0,
    text: String = "Hello",
    inputLanguage: String,
    outputLanguage: String,
    viewmodel: ConversationViewModel = viewModel()
) {
    var translatedText by remember { mutableStateOf("") }
    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            LanguagesUtils.translationInit(
                text = text,
                inputLanguage = inputLanguage,
                outputLanguage = outputLanguage,
                onSuccess = {translatedText = this}
            )
        }
    }
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = if (sender == 0) Arrangement.Start else Arrangement.End
    ) {
        if (sender == 0) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3162FF))
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = translatedText,
                textAlign = if (sender == 1) TextAlign.End else TextAlign.Start,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .align(if(sender == 1) Alignment.TopEnd else Alignment.TopStart),
            )
        }

        if (sender == 1) {
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF749F))
            )
        }
    }
}