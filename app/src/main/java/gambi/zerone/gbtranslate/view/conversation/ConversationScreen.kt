package gambi.zerone.gbtranslate.view.conversation

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.view.component.CupertinoAlertDialog

@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    application: Application,
    firstLanguage: String,
    secondLanguage: String,
    localizedContext: Context,
    toLanguageType: (LanguageType) -> Unit = {},
    onBack: () -> Unit,
    onExchange: () -> Unit,
    toHistoryScreen: () -> Unit
) {
    Content(
        application = application,
        firstLanguage = firstLanguage,
        secondLanguage = secondLanguage,
        toLanguageType = toLanguageType,
        toHistoryScreen = toHistoryScreen,
        onBack = onBack,
        onExchange = onExchange,
        localizedContext = localizedContext
    )
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    application: Application,
    firstLanguage: String,
    secondLanguage: String,
    localizedContext: Context,
    toLanguageType: (LanguageType) -> Unit = {},
    toHistoryScreen: () -> Unit,
    onBack: () -> Unit,
    onExchange: () -> Unit
) {
    var showWarningDialog by remember { mutableStateOf(false) }
    Column {
        Header(
            title = localizedContext.resources.getString(R.string.conversation),
            onBack = onBack,
            modifier = Modifier,
            trailing = {
                Icon(
                    painter = painterResource(R.drawable.ic_history),
                    contentDescription = "History Icon",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            toHistoryScreen()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_new_conversation),
                    contentDescription = "New Conversation Icon",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            showWarningDialog = true
                        }
                )
            }
        )
        Body(
            application = application,
            firstLanguage = firstLanguage,
            secondLanguage = secondLanguage,
            localizedContext = localizedContext,
            toLanguageType = toLanguageType,
            onExchange = onExchange
        )
    }
    if (showWarningDialog) {
        CupertinoAlertDialog(
            title = localizedContext.resources.getString(R.string.new_conversation),
            message = localizedContext.resources.getString(R.string.are_you_sure_you_want_to_start_a_new_conversation),
            onConfirm = {
                message.clear()
                showWarningDialog = false
            },
            onCancel = {
                showWarningDialog = false
            }
        )
    }
}

data class Message(
    val timestamp: Long = System.currentTimeMillis(),
    val text: String,
    val sender: Int,
)

val message = mutableStateListOf<Message>()

val messageSamples = listOf(
    Message(timestamp = 1, text = "Hello!", sender = 0),
    Message(timestamp = 2, text = "Hi there!", sender = 1),
    Message(timestamp = 3, text = "How are you?", sender = 0),
    Message(timestamp = 4, text = "I'm good, thanks! How about you?", sender = 1),
    Message(timestamp = 5, text = "I'm doing well too, thanks for asking!", sender = 0),
)
