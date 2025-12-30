package gambi.zerone.gbtranslate.view.home.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbtranslate.R

@Composable
fun TranslateModes(
    localizedContext: Context,
    toConversationScreen: () -> Unit = {},
    toCameraScreen: () -> Unit = {},
    toShowVoiceDialog: () -> Unit = {},
    toStudyScreen: () -> Unit = {}
) {
    Text(
        text = localizedContext.resources.getString(R.string.translation_modes),
        color = Color(0xff3162FF),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    ModeList(
        toConversationScreen = toConversationScreen,
        toCameraScreen = toCameraScreen,
        showVoiceDialog = toShowVoiceDialog,
        toStudyScreen = toStudyScreen,
        localizedContext = localizedContext
    )
}

@Composable
fun ModeList(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    toConversationScreen: () -> Unit,
    toCameraScreen: () -> Unit = {},
    showVoiceDialog: () -> Unit = {},
    toStudyScreen: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
//        ModeItem(
//            title = localizedContext.resources.getString(R.string.offline_mode),
//            icon = R.drawable.ic_airplane_stylist,
//            description = "Tran on to translate Offline",
//            modifier = Modifier.fillMaxWidth()
//        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ModeItem(
                title = localizedContext.resources.getString(R.string.camera_image),
                icon = R.drawable.ic_camera_stylist,
                description = "Scan and Translate",
                onClick = { toCameraScreen() },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ModeItem(
                title = localizedContext.resources.getString(R.string.voice),
                icon = R.drawable.ic_voice_stylist,
                description = "Speak to Translate",
                onClick = { showVoiceDialog() },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ModeItem(
                title = localizedContext.resources.getString(R.string.conversation),
                icon = R.drawable.ic_conversation_stylist,
                description = "Live Chat Translate",
                onClick = toConversationScreen,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ModeItem(
                title = localizedContext.resources.getString(R.string.study),
                onClick = toStudyScreen,
                description = "Learn & Review",
                icon = R.drawable.ic_study_stylist,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ModeItem(
    modifier: Modifier = Modifier,
    title: String = "title",
    description: String = "description",
    icon: Int = R.drawable.ic_camera_stylist,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Camera",
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
        )
        Column(modifier = Modifier.padding(end = 16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = description,
                color = Color(0xff9EA5AE),
                fontSize = 12.sp,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}