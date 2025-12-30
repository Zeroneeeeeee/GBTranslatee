package gambi.zerone.gbtranslate.view.conversation

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import gambi.zerone.gbtranslate.view.component.VoiceAccessDialog
import gambi.zerone.gbtranslate.view.home.SpeechDialog
import java.util.Locale

@Composable
fun Body(
    modifier: Modifier = Modifier,
    application: Application,
    firstLanguage: String,
    secondLanguage: String,
    localizedContext: Context,
    toLanguageType: (LanguageType) -> Unit = {},
    onExchange: () -> Unit = {}
) {
    var sender by remember { mutableStateOf(-1) }
    var showSpeechDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val micPermission = Manifest.permission.RECORD_AUDIO

    var showMicPermissionDialog by remember { mutableStateOf(false) }

    fun handleVoiceClick() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                micPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                showSpeechDialog = true
            }

            else -> {
                showMicPermissionDialog = true
            }
        }
    }

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            ConversationBox(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        rotationZ = 180f
                    },

                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                buttonColor = 0xFF3162FF,
                conversation = message,
                onClick = {
                    sender = 0
                    handleVoiceClick()
                },
                inputLanguage = secondLanguage,
                outputLanguage = firstLanguage,
                localizedContext = localizedContext
            )

            Box(modifier = Modifier.height(IntrinsicSize.Max)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable {
                                toLanguageType(LanguageType.INPUT)
                            }
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                drawLine(
                                    color = Color.White,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = strokeWidth
                                )
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = secondLanguage.toLanguageDisplayName(
                                Locale.forLanguageTag(
                                    secondLanguage
                                )
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .basicMarquee(),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable {
                                toLanguageType(LanguageType.OUTPUT)
                            }
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                drawLine(
                                    color = Color.White,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = strokeWidth
                                )
                            }
                            .graphicsLayer {
                                rotationZ = 180f
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = firstLanguage.toLanguageDisplayName(
                                Locale.forLanguageTag(
                                    firstLanguage
                                )
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .basicMarquee()
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .align(Alignment.Center)
                        .clickable {
                            onExchange()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_exchange),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            ConversationBox(
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                buttonColor = 0xFFFF749F,
                conversation = message,
                onClick = {
                    sender = 1
                    handleVoiceClick()
                },
                inputLanguage = firstLanguage,
                outputLanguage = secondLanguage,
                localizedContext = localizedContext
            )
        }
        if (showSpeechDialog) {
            SpeechDialog(
                localizedContext = localizedContext,
                application = application,
                buttonColor = if (sender == 0) Color(0xFF3162FF) else Color(0xFFFF749F),
                fadeColor = if (sender == 0) Color(0xFF7B97F7) else Color(0xFFFF9BA6),
                onTextReceived = {
                    message.add(Message(text = it, sender = sender))
                    Log.d("TAG", "ConversationBox: ${message}")
                },
                language = if (sender == 0) firstLanguage else secondLanguage,
                onDismiss = {
                    showSpeechDialog = false
                },
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (sender == 1) 0f else 180f
                }
            )
        }
        if (showMicPermissionDialog) {
            VoiceAccessDialog(
                localizedContext = localizedContext,
                onGranted = {
                    showSpeechDialog = true
                },
                onDismiss = {
                    showMicPermissionDialog = false
                }
            )
        }
    }

}