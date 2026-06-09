package industrial.einhorn.mjolnir.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// EINHORN palette: deep navy + crimson accent
private val EinhornDarkColors = darkColorScheme(
    primary = Color(0xFFE94560),
    onPrimary = Color(0xFF1A1A2E),
    primaryContainer = Color(0xFF4A1020),
    onPrimaryContainer = Color(0xFFFFB3C1),
    secondary = Color(0xFF4CC9F0),
    onSecondary = Color(0xFF001A2E),
    secondaryContainer = Color(0xFF003550),
    onSecondaryContainer = Color(0xFFB3E5FC),
    tertiary = Color(0xFFF7B731),
    background = Color(0xFF0F0F1A),
    surface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFF252540),
    onBackground = Color(0xFFE8E8F0),
    onSurface = Color(0xFFE8E8F0),
    onSurfaceVariant = Color(0xFFAAAAAF),
    error = Color(0xFFFF6B6B),
)

@Composable
fun MjolnirTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EinhornDarkColors,
        content = content
    )
}
