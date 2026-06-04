package cz.ackee.strava.wearos.core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun StravaTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(LocalColors provides designSystemColors()) {
            CompositionLocalProvider(LocalTypography provides designSystemTypography()) {
                content()
            }
        }
    }
}

private val LocalColors = staticCompositionLocalOf { Colors() }
private val LocalTypography = staticCompositionLocalOf { Typography() }

object StravaTheme {

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}
