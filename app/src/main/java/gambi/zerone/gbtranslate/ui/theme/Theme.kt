package gambi.zerone.gbtranslate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3162FF),
    secondary = Color(0xFFFCC443),
    primaryContainer = Color(0xFFECF3FE),// sẽ sửa thành primary container
    secondaryContainer = Color(0xFFF8EFFF),// sẽ sửa thành secondary container
    surface = Color(0xFFECF3FE),
    background = Color.White,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFCC443),
    secondary = Color(0xFFFFFFFF).copy(alpha = 0.25f),
    primaryContainer = Color(0xFF000540),
    secondaryContainer = Color(0xFF484F67),
    background = Color(0xFF141414),
    surface = Color(0xFFECF3FE),
    tertiary = Pink80
)

@Composable
fun GBTranslateTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}