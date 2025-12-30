package gambi.zerone.gbtranslate.view.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gbtranslate.R


@Composable
fun MainHeader(
    modifier: Modifier = Modifier,
    onSettingClick: () -> Unit,
    toHistoryScreen: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_crown),
            contentDescription = "Premium Icon",
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.ic_history),
            contentDescription = "History Icon",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    toHistoryScreen()
                },
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(R.drawable.ic_setting),
            contentDescription = "Setting Icon",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(32.dp)
                .clickable { onSettingClick() },
        )
    }
}