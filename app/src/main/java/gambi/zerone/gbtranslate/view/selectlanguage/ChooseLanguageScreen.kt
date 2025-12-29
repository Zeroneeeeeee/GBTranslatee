package gambi.zerone.gbtranslate.view.selectlanguage

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.view.component.CupertinoAlertDialog
import gambi.zerone.gbtranslate.view.component.LoadingScreen
import gambi.zerone.gbtranslate.utils.LanguagesUtils
import gambi.zerone.gbtranslate.utils.RecentLanguagePrefs
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import gambi.zerone.gbtranslate.view.conversation.Header
import java.util.Locale

@Composable
fun ChooseLanguageScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectLanguageViewModel = viewModel(),
    localizedContext: Context,
    toTranslateScreen: (String) -> Unit,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var languageCode by remember { mutableStateOf("")}

    val context = LocalContext.current

    Content(
        localizeContext = localizedContext,
        getLanguage = {
            languageCode = it
            toTranslateScreen(it)
        },
        onLoading = { isLoading = it },
        onBack = onBack,
        onDownload = {
            languageCode = it
            showDownloadDialog = true
        },
        onDelete = {
            languageCode = it
            showDeleteDialog = true
        }
    )
    if (isLoading) {
        LoadingScreen()
    }
    if (showDownloadDialog) {
        CupertinoAlertDialog(
            title = localizedContext.resources.getString(R.string.download_language_model),
            message = localizedContext.resources.getString(
                R.string.do_you_want_to_download_the,
                languageCode.toLanguageDisplayName()
            ),
            confirmText = localizedContext.resources.getString(R.string.download),
            cancelText = localizedContext.resources.getString(R.string.cancel),
            onConfirm = {
                viewModel.startDownload(languageCode)

                LanguagesUtils.downloadLanguageModel(
                    languageCode,
                    onSuccess = {
                        viewModel.downloadSuccess(languageCode)
                        Toast.makeText(
                            context,
                            localizedContext.resources.getString(R.string.download_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFailure = {
                        viewModel.downloadFailed(languageCode)
                        Toast.makeText(
                            context,
                            it.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )
    }
    if (showDeleteDialog) {
        CupertinoAlertDialog(
            title = localizedContext.resources.getString(R.string.delete_language_model),
            message = localizedContext.resources.getString(
                R.string.do_you_want_to_delete_the,
                languageCode.toLanguageDisplayName()
            ),
            confirmText = localizedContext.resources.getString(R.string.delete),
            cancelText = localizedContext.resources.getString(R.string.cancel),
            onConfirm = {
                viewModel.deleteLanguage(languageCode)
            }
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    onDownload: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    getLanguage: (String) -> Unit = {},
    onLoading: (Boolean) -> Unit = {},
    onBack: () -> Unit = {},
    localizeContext: Context
) {
    var search by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header(title = localizeContext.resources.getString(R.string.select_language), onBack = onBack) {

        }
        SearchBar(
            getSearchResult = { search = it },
            localizeContext = localizeContext,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LanguageList(
            filter = search,
            getLanguage = getLanguage,
            onLoading = onLoading,
            onDownload = onDownload,
            onDelete = onDelete,
            localizeContext = localizeContext,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier,localizeContext: Context, getSearchResult: (String) -> Unit = {}) {
    var search by remember { mutableStateOf("") }
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                getSearchResult(it)
            },
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(localizeContext.resources.getString(R.string.search_language)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LanguageList(
    modifier: Modifier = Modifier,
    filter: String = "",
    localizeContext: Context,
    getLanguage: (String) -> Unit = {},
    onLoading: (Boolean) -> Unit,
    onDownload: (String) -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    val context = LocalContext.current
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            Text(text = localizeContext.resources.getString(R.string.recent_languages), color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                val recentLanguages = RecentLanguagePrefs.get(context)
                var chosenLanguage by remember { mutableStateOf("") }

                recentLanguages
                    .filter { it.toLanguageDisplayName().lowercase().contains(filter) }
                    .forEach { language ->
                        LanguageItem(
                            languageCode = language,
                            onDownload = onDownload,
                            onDelete = onDelete,
                            getLanguage = {
                                chosenLanguage = it
                                getLanguage(it)
                            },
                            modifier = Modifier.background(
                                if (chosenLanguage == language)
                                    MaterialTheme.colorScheme.surface
                                else
                                    Color.Transparent
                            )
                        )
                    }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = localizeContext.resources.getString(R.string.all_languages), color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                var chosenLanguage by remember { mutableStateOf("") }
                LanguagesUtils.getAllLanguagesCode()
                    .filter { it.toLanguageDisplayName().lowercase().contains(filter) }
                    .forEach { language ->
                        LanguageItem(
                            languageCode = language,
                            onDownload = onDownload,
                            onDelete = onDelete,
                            getLanguage = {
                                chosenLanguage = it
                                getLanguage(it)
                            },
                            modifier = Modifier.background(
                                if (chosenLanguage == language) Color(
                                    0xFFCFE0FC
                                ) else Color.Transparent
                            ),
                        )
                    }
            }
        }
    }
}

@Composable
private fun LanguageItem(
    modifier: Modifier = Modifier,
    languageCode: String,
    getLanguage: (String) -> Unit,
    onDownload: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    viewModel: SelectLanguageViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.getState(languageCode)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = state != DownloadState.Downloading
            ) {
                when (state) {
                    DownloadState.Downloaded -> {
                        getLanguage(languageCode)
                        RecentLanguagePrefs.save(context, languageCode)
                    }

                    DownloadState.NotDownloaded -> {
                        onDownload(languageCode)
                    }

                    DownloadState.Downloading -> Unit

                    DownloadState.Loading -> Unit
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = languageCode.toLanguageDisplayName(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = languageCode.toLanguageDisplayName(
                    Locale.forLanguageTag(languageCode)
                ),
                color = Color.Gray
            )
        }

        when (state) {
            DownloadState.NotDownloaded ->
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.padding(16.dp)
                )

            DownloadState.Downloading ->
                DownloadGradientIcon(modifier = Modifier.padding(16.dp))

            DownloadState.Downloaded -> {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable(
                            onClick = {
                                onDelete(languageCode)
                            }
                        )
                )
            }

            DownloadState.Loading -> Unit
        }
    }
}


@Composable
fun DownloadGradientIcon(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "download")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Icon(
        painter = painterResource(id = R.drawable.ic_download),
        contentDescription = null,
        tint = Color.LightGray,
        modifier = modifier
            .size(24.dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                val brush = Brush.verticalGradient(
                    colors = listOf(Color.Blue, Color.LightGray),
                    startY = 0f,
                    endY = size.height * progress
                )
                drawRect(
                    brush = brush,
                    blendMode = BlendMode.SrcIn
                )
            }
    )
}