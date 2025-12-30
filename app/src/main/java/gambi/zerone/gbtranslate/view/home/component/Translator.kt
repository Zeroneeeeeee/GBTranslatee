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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.LanguageType
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import gambi.zerone.gbtranslate.view.home.HomeViewModel

@Composable
fun Translator(
    modifier: Modifier = Modifier,
    toChoosingLanguage: (LanguageType) -> Unit,
    toTextTranslate: () -> Unit,
    inputLanguage: String,
    outputLanguage: String,
    inputText: String,
    localizedContext: Context,
    getInputText: (String) -> Unit,
    onVoiceToText: () -> Unit,
    onExchange: () -> Unit,
    toCameraScreen: () -> Unit,
    viewmodel: HomeViewModel = viewModel()
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TranslateLanguage(
            onClick = toChoosingLanguage,
            inputLanguage = inputLanguage,
            outputLanguage = outputLanguage,
            onExchange = onExchange
        )
        Spacer(modifier = Modifier.height(16.dp))
        TranslateTextField(
            toTextTranslate = {
                getInputText(it)
                toTextTranslate()
//                LanguagesUtils.translationInit(
//                    text = it,
//                    inputLanguage = inputLanguage,
//                    outputLanguage = outputLanguage,
//                    onSuccess = {
//
//                        viewmodel.insertHistory(
//                            TranslateHistory(
//                                inputText = it,
//                                outputText = this,
//                                inputLanguage = inputLanguage,
//                                outputLanguage = outputLanguage
//                            )
//                        )
//                    }
//                )

            },
            onVoiceToText = onVoiceToText,
            inputText = inputText,
            toCameraScreen = toCameraScreen,
            localizedContext = localizedContext
        )
    }
}

@Composable
fun TranslateLanguage(
    modifier: Modifier = Modifier,
    onClick: (LanguageType) -> Unit,
    inputLanguage: String,
    outputLanguage: String,
    onExchange: () -> Unit
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
    LocalContext.current
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
            tint = Color.Unspecified,
            contentDescription = "Down arrow"
        )
    }
}

@Composable
fun ExchangeButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier.background(Color(0xFF3162FF), CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_exchange),
            tint = Color.White,
            contentDescription = "Exchange"
        )
    }
}

@Composable
fun TranslateTextField(
    modifier: Modifier = Modifier,
    inputText: String = "",
    localizedContext: Context,
    toTextTranslate: (String) -> Unit,
    onVoiceToText: () -> Unit,
    toCameraScreen: () -> Unit,

    ) {
    var text by remember { mutableStateOf(inputText) }
    LaunchedEffect(inputText) {
        text = inputText
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(20.dp)
            )
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    text = localizedContext.resources.getString(R.string.enter_text_to_translate),
                    color = Color(0xff9EA5AE)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp, end = 16.dp)
        ) {
            if (text.isEmpty()) {
                IconButton(
                    onClick = onVoiceToText,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .size(34.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic_fill),
                        contentDescription = "Microphone",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        toCameraScreen()
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .size(34.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera_fill),
                        contentDescription = "Camera",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else {
                Button(
                    onClick = {
                        toTextTranslate(text)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFCC443),
                        contentColor = Color.Black
                    )

                ) {
                    Text(text = "Translate")
                }
            }
        }
    }
}