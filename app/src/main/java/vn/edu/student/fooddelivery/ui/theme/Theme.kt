package vn.edu.student.fooddelivery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Orange40,
    onPrimary = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = Neutral10,
    background = Neutral90,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    onSurfaceVariant = Neutral40,
    error = StatusCancelled,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Orange80,
    onPrimary = Neutral10,
    primaryContainer = Orange40,
    onPrimaryContainer = Color.White,
    background = Color(0xFF1C1B1A),
    onBackground = Neutral90,
    surface = Color(0xFF2A2826),
    onSurface = Neutral90,
    onSurfaceVariant = Color(0xFFC9C6C2),
    error = StatusCancelled,
    onError = Color.White
)

@Composable
fun FoodDeliveryTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FoodDeliveryTypography,
        shapes = DeliveryShapes,
        content = content
    )
}
