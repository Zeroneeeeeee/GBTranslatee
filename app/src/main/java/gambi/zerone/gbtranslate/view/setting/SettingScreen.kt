package gambi.zerone.gbtranslate.view.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.gbtranslate.R
import gambi.zerone.gbtranslate.utils.SharedPreference
import gambi.zerone.gbtranslate.utils.toLanguageDisplayName
import gambi.zerone.gbtranslate.view.conversation.Header

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    activity: Activity,
    toLanguageScreen: () -> Unit,
    onBack: () -> Unit,
    onChangeMode: (UiMode) -> Unit
) {
    Content(
        localizedContext = localizedContext,
        toLanguageScreen = toLanguageScreen,
        onBack = onBack,
        activity = activity,
        onChangeMode = onChangeMode
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    activity: Activity,
    toLanguageScreen: () -> Unit,
    onBack: () -> Unit,
    onChangeMode: (UiMode) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Header(title = localizedContext.resources.getString(R.string.setting), onBack = onBack)
        ListSettingItem(
            activity = activity,
            localizedContext = localizedContext,
            toLanguageScreen = toLanguageScreen,
            onChangeMode = onChangeMode
        )
    }
}

@Composable
fun ListSettingItem(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    activity: Activity,
    toLanguageScreen: () -> Unit = {},
    onChangeMode: (UiMode) -> Unit = {}
) {
    val context = LocalContext.current
    var isDarkMode by remember { mutableStateOf(SharedPreference.getUiMode(context) == UiMode.DARK) }

    val items = listOf(
        ItemData(R.drawable.ic_language, localizedContext.resources.getString(R.string.language)),
        ItemData(R.drawable.ic_darkmode, localizedContext.resources.getString(R.string.dark_mode)),
        ItemData(
            R.drawable.ic_feedback,
            localizedContext.resources.getString(R.string.send_feedback)
        ),
        ItemData(
            R.drawable.ic_policy,
            localizedContext.resources.getString(R.string.privacy_policy)
        ),
        ItemData(R.drawable.ic_info, localizedContext.resources.getString(R.string.about_app)),
        ItemData(
            R.drawable.ic_share_outline,
            localizedContext.resources.getString(R.string.share_app)
        ),
        ItemData(R.drawable.ic_rate, localizedContext.resources.getString(R.string.rate_us)),
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Item(
            icon = items[0].icon,
            title = items[0].title,
            onClick = toLanguageScreen,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = SharedPreference.getLanguage(localizedContext)?.toLanguageDisplayName()
                    ?: "English",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
            Icon(
                painter = painterResource(R.drawable.ic_right_navigate),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Item(
            icon = items[1].icon,
            title = items[1].title,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Switch(
                checked = isDarkMode,
                onCheckedChange = {
                    isDarkMode = it
                    when (it) {
                        true -> {
                            SharedPreference.saveUiMode(context, UiMode.DARK)
                            onChangeMode(UiMode.DARK)
                        }

                        false -> {
                            SharedPreference.saveUiMode(context, UiMode.LIGHT)
                            onChangeMode(UiMode.LIGHT)
                        }
                    }
                },
                modifier = Modifier.height(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )
        ) {
            for (i in 2..4) {
                Item(
                    icon = items[i].icon,
                    title = items[i].title,
                    onClick = {
                        when (i) {
                            2 -> {
                                /*Feedback*/
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        "https://play.google.com/store/apps/details?id=gambi.zerone.gbtranslate".toUri()
                                    )
                                )
                            }

                            3 -> {
                                /*Policy*/
                                val url = "https://gambi-publishing-app.web.app/privacy-policy.html"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = url.toUri()
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "No app can handle this action.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            4 -> {
                                /*Detail*/
                            }
                        }
                    }
                )
                if (i != 4)
                    HorizontalDivider()
            }
        }
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )

        ) {
            for (i in 5..6) {
                Item(
                    icon = items[i].icon,
                    title = items[i].title,
                    onClick = {
                        when (i) {
                            5 -> {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "https://play.google.com/store/apps/details?id=gambi.zerone.gbtranslate"
                                    )
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }

                            6 -> {
                                /*Rate us*/
                            }
                        }
                    }
                )
                if (i != 6)
                    HorizontalDivider()

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Item(
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.ic_setting,
    title: String = "Setting",
    onClick: () -> Unit = {},
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Leading Icon",
            tint = Color(0xFF9EA5AE)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier)
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}

data class ItemData(
    val icon: Int,
    val title: String,
)

