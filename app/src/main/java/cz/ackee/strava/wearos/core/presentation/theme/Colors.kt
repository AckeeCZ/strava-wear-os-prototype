package cz.ackee.strava.wearos.core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Colors(
    val accent: Color,
    val onAccent: Color,
    val backgrounds: Backgrounds,
    val foregrounds: Foregrounds,
    val map: Map,
) {

    constructor() : this(
        accent = Color.Unspecified,
        onAccent = Color.Unspecified,
        backgrounds = Backgrounds(),
        foregrounds = Foregrounds(),
        map = Map(),
    )

    @Immutable
    data class Backgrounds(
        val primary: Color,
        val surface: Color,
        val surfaceVariant: Color,
    ) {

        constructor() : this(
            primary = Color.Unspecified,
            surface = Color.Unspecified,
            surfaceVariant = Color.Unspecified,
        )
    }

    @Immutable
    data class Foregrounds(
        val primary: Color,
        val secondary: Color,
    ) {

        constructor() : this(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
        )
    }

    @Immutable
    data class Map(
        val polyline: Color,
    ) {

        constructor() : this(polyline = Color.Unspecified)
    }
}

@Suppress("MagicNumber")
@Composable
fun designSystemDarkColors(): Colors = Colors(
    accent = Color(0xFFFC5200),
    onAccent = Color(0xFFFFFFFF),
    backgrounds = Colors.Backgrounds(
        primary = Color(0xFF0D0D0D),
        surface = Color(0xFF1C1C1E),
        surfaceVariant = Color(0xFF2C2C2E),
    ),
    foregrounds = Colors.Foregrounds(
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFA1A1A6),
    ),
    map = Colors.Map(polyline = Color(0xFFFC5200)),
)

@Suppress("MagicNumber")
@Composable
fun designSystemLightColors(): Colors = Colors(
    accent = Color(0xFFFC5200),
    onAccent = Color(0xFFFFFFFF),
    backgrounds = Colors.Backgrounds(
        primary = Color(0xFFFFFFFF),
        surface = Color(0xFFF2F2F7),
        surfaceVariant = Color(0xFFE5E5EA),
    ),
    foregrounds = Colors.Foregrounds(
        primary = Color(0xFF000000),
        secondary = Color(0xFF6E6E73),
    ),
    map = Colors.Map(polyline = Color(0xFFFC5200)),
)
