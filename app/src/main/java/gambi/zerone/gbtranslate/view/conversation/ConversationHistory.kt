package gambi.zerone.gbtranslate.view.conversation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import com.google.mlkit.nl.translate.TranslateLanguage
import gambi.zerone.gbtranslate.utils.LanguagesUtils

@Composable
fun ConversationHistoryScreen(modifier: Modifier = Modifier, localizedContext: Context, onBack: () -> Unit){
    Column(modifier = modifier
        .fillMaxSize()
    ){
        Header(
            title = localizedContext.resources.getString(R.string.history),
            onBack = onBack
        )
        Body(
            conversation = message,
            localizedContext = localizedContext
        )
    }
}

@Composable
fun Body(
    modifier: Modifier = Modifier,
    conversation: List<Message> = messageSamples,
    inputLanguage: String = TranslateLanguage.ENGLISH,
    outputLanguage: String = TranslateLanguage.VIETNAMESE,
    localizedContext: Context
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HistoryConversations(
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
                    text = localizedContext.resources.getString(R.string.you_have_no_conversations_yet),
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryConversations(
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
        items(conversation) {
            HistoryConversationChat(
                text = it.text,
                sender = it.sender,
                inputLanguage = inputLanguage,
                outputLanguage = outputLanguage
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HistoryConversationChat(
    modifier: Modifier = Modifier,
    sender: Int = 0,
    text: String = "Hello",
    inputLanguage: String,
    outputLanguage: String,
    viewModel: ConversationViewModel = viewModel()
) {
    var translatedText by remember { mutableStateOf("") }
    LaunchedEffect(text) {

        if (text.isNotEmpty()) {
            if(sender == 1){

                LanguagesUtils.translationInit(
                    text = text,
                    inputLanguage = inputLanguage,
                    outputLanguage = outputLanguage,
                    onSuccess = { translatedText = this }
                )
            }
            else{
                LanguagesUtils.translationInit(
                    text = text,
                    inputLanguage = outputLanguage,
                    outputLanguage = inputLanguage,
                    onSuccess = { translatedText = this }
                )
            }
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

        Column(
            modifier = Modifier
                .width(intrinsicSize = IntrinsicSize.Max)
                .background(
                    if (sender == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(12.dp)
                ),
        ) {
            Text(
                text = text,
                textAlign = if (sender == 1) TextAlign.End else TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF9EA5AE))
            )
            Text(
                text = translatedText,
                textAlign = if (sender == 1) TextAlign.End else TextAlign.Start,
                color = Color(0xFF3162FF),
                modifier = Modifier.padding(16.dp)
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