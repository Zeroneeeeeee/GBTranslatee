package gambi.zerone.gbtranslate.view.texttranslator.component

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.entity.TranslateHistory
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.LanguagesUtils
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.utils.textToSpeech
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import gambi.zerone.gbtranslate.view.texttranslator.TextTranslatorViewModel
import java.util.Locale

@Composable
fun Translator(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    toChoosingLanguage: (LanguageType) -> Unit,
    inputLanguage: String,
    outputLanguage: String,
    input: String,
    output: String,
    onExchange: () -> Unit,
    onVoiceToText: () -> Unit,
    onCamera: () -> Unit
) {
    var text by remember { mutableStateOf(input) }
    var outputText by remember { mutableStateOf(output) }
    LaunchedEffect(Unit) {
        Log.d("TranslateTextField1", "output: $output")
    }
//    LaunchedEffect(input) {
//        text = input
//    }
//    LaunchedEffect(output) {
//        outputText = output
//    }

    Column(modifier = modifier.fillMaxWidth()) {
        TranslateLanguage(
            onClick = toChoosingLanguage,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage,
            onExchange = {
                onExchange()
                val temp = text
                text = outputText
                outputText = temp
                Log.d("TranslateTextField1", "output: $output")
                Log.d("TranslateTextField1", "text: $text")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TranslateTextField(
            localizedContext = localizedContext,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage,
            input = text,
            output = outputText,
            getText = {input, output ->
                text = input
                outputText = output
            },
            onVoiceToText = onVoiceToText,
            onCamera = onCamera,
        )
    }
}

@Composable
fun TranslateLanguage(
    modifier: Modifier = Modifier,
    onClick: (LanguageType) -> Unit,
    inputLanguage: String,
    outputLanguage: String,
    onExchange: () -> Unit,
    viewModel: TextTranslatorViewModel = viewModel()
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LanguagePicker(
            modifier = Modifier.weight(1f),
            onClick = { onClick(LanguageType.INPUT) },
            language = inputLanguage
        )
        ExchangeButton(onClick = onExchange)
        LanguagePicker(
            modifier = Modifier.weight(1f),
            onClick = { onClick(LanguageType.OUTPUT) },
            language = outputLanguage
        )
    }
}

@Composable
fun LanguagePicker(
    modifier: Modifier = Modifier,
    language: String = "English",
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp))
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language.toLanguageDisplayName(),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .basicMarquee()
        )
        Icon(
            painter = painterResource(R.drawable.ic_down_navigate),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = "Down arrow"
        )
    }
}

@Composable
fun ExchangeButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier.background(Color(0xff3162FF), CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_exchange),
            tint = Color.Unspecified,
            contentDescription = "Exchange"
        )
    }
}

@Composable
fun TranslateTextField(
    modifier: Modifier = Modifier,
    inputLanguage: String,
    outputLanguage: String,
    input: String,
    output: String,
    localizedContext: Context,
    getText: (String, String) -> Unit,
    onVoiceToText: () -> Unit,
    onCamera: () -> Unit,
    viewModel: TextTranslatorViewModel = viewModel()
) {
    val context = LocalContext.current
    val inputTTS = remember { mutableStateOf<TextToSpeech?>(null) }
    val outputTTS = remember { mutableStateOf<TextToSpeech?>(null) }
    var inputText by remember { mutableStateOf(input) }
    var outputText by remember { mutableStateOf(output) }
    var isUserInput by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("TranslateTextField2", "output: $output")
        Log.d("Input", "input: $input")
        isLoading = true
        LanguagesUtils.translationInit(
            text = input,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage,
            onSuccess = {
                outputText = this
                getText(inputText, outputText)
                viewModel.insertHistory(
                    TranslateHistory(
                        inputText = input,
                        outputText = this,
                        inputLanguage = inputLanguage,
                        outputLanguage = outputLanguage
                    )
                )
                isLoading = false
            }
        )
    }

    LaunchedEffect(input) {
        inputText = input
    }

    LaunchedEffect(output) {
        outputText = output
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {
        InputField(
            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            text = inputText,
            speakerClick = {
                textToSpeech(inputTTS, context, inputLanguage, inputText)
            },
            onTextChange = {
                outputText = ""
                inputText = it
            },
            localizedContext = localizedContext,
        ) {
            if (inputText.isEmpty()) {
                IconButton(
                    onClick = onVoiceToText,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic_outline),
                        contentDescription = "Mic",
                        tint = Color(0xFF9EA5AE)
                    )
                }
                IconButton(
                    onClick = onCamera,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera_outlined),
                        contentDescription = "Camera",
                        tint = Color(0xFF9EA5AE)
                    )
                }
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(text = inputText))
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy_outline),
                        contentDescription = "Copy",
                        tint = Color(0xFF9EA5AE)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        isLoading = true
                        LanguagesUtils.translationInit(
                            text = inputText,
                            inputLanguage = inputLanguage,
                            outputLanguage = outputLanguage,
                            onSuccess = {
                                outputText = this
                                getText(inputText, outputText)
                                viewModel.insertHistory(
                                    TranslateHistory(
                                        inputText = inputText,
                                        outputText = outputText,
                                        inputLanguage = inputLanguage,
                                        outputLanguage = outputLanguage
                                    )
                                )
                                isLoading = false
                            }
                        )

                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_send_outline),
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        InputField(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            canType = false,
            text = if(isLoading) "Translating..." else outputText,
            speakerClick = {
                textToSpeech(outputTTS, context, outputLanguage, outputText)
            },
            localizedContext = localizedContext,
        ) {
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_outline),
                    contentDescription = "Favorite",
                    tint = Color(0xFF9EA5AE)
                )
            }
            IconButton(
                onClick = {
                    clipboard.setText(AnnotatedString(text = inputText))
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy_outline),
                    contentDescription = "Copy",
                    tint = Color(0xFF9EA5AE)
                )
            }

        }
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    canType: Boolean = true,
    text: String = "",
    onTextChange: (String) -> Unit = {},
    speakerClick: () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            enabled = canType,
            placeholder = {
                Text(
                    text = if (canType) {
                        localizedContext.resources.getString(R.string.enter_text_to_translate)
                    } else {
                        ""
                    },
                    color = Color(0xff9EA5AE)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = speakerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_speaker),
                    contentDescription = "Speaker",
                    tint = Color(0xFF9EA5AE)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            trailingIcon()
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

