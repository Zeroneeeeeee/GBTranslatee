package gambi.zerone.gbtranslate.view.translatehistory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.entity.TranslateHistory
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import gambi.zerone.gbtranslate.view.conversation.Header
import gambi.zerone.gbtranslate.view.setting.UiMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TranslateHistoryScreen(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    onBack: () -> Unit = {},
    toTranslateScreen: (TranslateHistory) -> Unit = {},
    viewModel: TranslateHistoryViewModel = viewModel()
) {
    val histories by viewModel.history.collectAsState()
    val timestamps by viewModel.timestamps.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var deletedHistory by remember { mutableStateOf<TranslateHistory?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
        viewModel.fetchTimestamps()
    }

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Header(
                modifier = Modifier,
                title = localizedContext.resources.getString(R.string.translate_history),
                onBack = onBack,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(
                localizedContext = localizedContext,
                modifier = Modifier.padding(horizontal = 16.dp),
                text = searchText,
                onTextChange = { searchText = it })
            Spacer(modifier = Modifier.height(8.dp))
            HistoryList(
                modifier = Modifier.padding(horizontal = 16.dp),
                histories = histories,
                timestamps = timestamps,
                searchText = searchText,
                onClick = { toTranslateScreen(it) },
                onDelete = {
                    showDeleteDialog = true
                    deletedHistory = it
                }
            )
        }
        if (showDeleteDialog) {
            NotifyDialog(
                title = localizedContext.resources.getString(R.string.delete),
                description = localizedContext.resources.getString(R.string.are_you_sure_you_want_to_delete_translate_history),
                confirmText = localizedContext.resources.getString(R.string.delete),
                cancelText = localizedContext.resources.getString(R.string.cancel),
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteHistory(deletedHistory!!)
                    showDeleteDialog = false
                }
            )
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    text: String = "",
    onTextChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                text = localizedContext.resources.getString(R.string.search),
                color = Color(0xFF9EA5AE)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
    )
}

@Preview(showBackground = true)
@Composable
fun HistoryList(
    modifier: Modifier = Modifier,
    histories: List<TranslateHistory> = listHistorySample,
    timestamps: List<String> = listTimestampSample,
    searchText: String = "",
    onClick: (TranslateHistory) -> Unit = {},
    onDelete: (TranslateHistory) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(timestamps) { timestamp ->
            Text(
                text = timestamp,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                histories.filter {
                    it.id.toDateString() == timestamp &&
                            (it.inputText.lowercase().contains(searchText.lowercase()) ||
                                    it.outputText.lowercase().contains(searchText.lowercase()))
                }.forEach {
                    HistoryItem(history = it, onDelete = onDelete, onClick = onClick)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    history: TranslateHistory = TranslateHistory(
        inputLanguage = "English",
        outputLanguage = "Vietnamese",
        inputText = "Hello",
        outputText = "Xin chào"
    ),
    onClick: (TranslateHistory) -> Unit = {},
    onDelete: (TranslateHistory) -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
            .clickable { onClick(history) }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = history.inputLanguage.toLanguageDisplayName(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(R.drawable.ic_exchange),
                contentDescription = "Exchange Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = history.outputLanguage.toLanguageDisplayName(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete Icon",
                tint = Color(0xFF9EA5AE),
                modifier = Modifier.clickable { onDelete(history) }
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(text = history.inputText, color = Color(0xFF9EA5AE))
        Spacer(
            Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
        Text(
            text = history.outputText,
            color = if (SharedPreference.getUiMode(context) == UiMode.DARK) Color.White else Color(
                0xFF3162FF
            )
        )
    }
}

fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

val listHistorySample = listOf(
    TranslateHistory(
        inputLanguage = "en",
        outputLanguage = "vi",
        inputText = "Hello",
        outputText = "Xin chào"
    ),
    TranslateHistory(
        inputLanguage = "en",
        outputLanguage = "vi",
        inputText = "Goodbye",
        outputText = "Tạm biệt"
    )
)

val listTimestampSample = listOf(
    "17/12/2025",
    "16/12/2025"
)