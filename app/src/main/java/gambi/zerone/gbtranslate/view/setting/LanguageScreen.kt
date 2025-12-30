package gambi.zerone.gbtranslate.view.setting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.view.conversation.Header

class Language(
    val icon: Int,
    val language: String,
    val id: String
)

val languages = listOf(
    Language(R.drawable.ic_us, "English", "en"),
    Language(R.drawable.ic_spain, "Español", "es"),
    Language(R.drawable.ic_korea, "한국어", "ko"),
    Language(R.drawable.ic_germany, "Deutsch", "de"),
    Language(R.drawable.ic_france, "Français", "fr"),
    Language(R.drawable.ic_canada, "Anglais", "ca"),
    Language(R.drawable.ic_portuguese, "Português", "pt"),
    Language(R.drawable.ic_finland, "Suomi", "fi"),
    Language(R.drawable.ic_japan, "日本語", "ja"),
    Language(R.drawable.ic_vietnam, "Tiếng Việt", "vi"),
)

@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    language: String,
    hideBackButton: Boolean,
    getLocale: (String) -> Unit,
    onBack: () -> Unit,
    onChecked: () -> Unit
) {
    var chosenLanguage by remember { mutableStateOf("") }
    var enable by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Header(
            title = localizedContext.resources.getString(R.string.language),
            onBack = onBack,
            hideBackButton = hideBackButton,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Check",
                tint = if (enable) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.clickable(
                    enabled = enable,
                    onClick = {
                        SharedPreference.saveLanguage(localizedContext, chosenLanguage)
                        getLocale(chosenLanguage)
                        onChecked()
                    }
                )
            )
        }
        LanguageList(
            modifier = modifier,
            localizedContext = localizedContext,
            language = language,
            getLocale = getLocale,
            getChosenLanguage = {
                enable = true
                chosenLanguage = it
            }
        )
    }
}

@Composable
fun LanguageList(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    language: String,
    getLocale: (String) -> Unit,
    getChosenLanguage: (String) -> Unit = {}
) {
    var selected by remember { mutableStateOf(language) }
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(languages) { language ->
            LanguageItem(
                language = language.language,
                icon = language.icon,
                isSelected = selected == language.id,
                onSelected = {
                    selected = language.id
                    getChosenLanguage(language.id)
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(
                        color = if (selected == language.id) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    language: String = "English",
    icon: Int = R.drawable.ic_launcher_background,
    isSelected: Boolean = false,
    onSelected: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier
                .size(36.dp)
                .clip(shape = CircleShape)
                .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = language,
            fontSize = 18.sp,
            color = if (isSelected) Color(0xFF3162FF) else Color(0xFFADADAD)
        )
        Spacer(modifier = Modifier.weight(1f))
        CustomRadioButton(
            selected = isSelected,
            onClick = onSelected,
//            colors = RadioButtonDefaults.colors(
//                selectedColor = MaterialTheme.colorScheme.onBackground,
//                unselectedColor = MaterialTheme.colorScheme.onBackground,
//            )
        )
    }
}

@Composable
fun CustomRadioButton(modifier: Modifier = Modifier, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_tick),
                contentDescription = "Tick",
                tint = Color.Unspecified
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_untick),
                contentDescription = "Untick",
                tint = Color.Unspecified
            )
        }
    }
}