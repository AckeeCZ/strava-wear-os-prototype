package cz.ackee.strava.wearos.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class Typography(
    val headlines: Headlines,
    val paragraphs: Paragraphs,
    val labels: Labels,
) {

    constructor() : this(Headlines(), Paragraphs(), Labels())

    @Immutable
    data class Headlines(
        val display: TextStyle,
        val title: TextStyle,
        val subtitle: TextStyle,
    ) {

        constructor() : this(
            display = TextStyle.Default,
            title = TextStyle.Default,
            subtitle = TextStyle.Default,
        )
    }

    @Immutable
    data class Paragraphs(
        val body: TextStyle,
    ) {

        constructor() : this(body = TextStyle.Default)
    }

    @Immutable
    data class Labels(
        val large: TextStyle,
        val small: TextStyle,
    ) {

        constructor() : this(
            large = TextStyle.Default,
            small = TextStyle.Default,
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun designSystemTypography(): Typography {
    val sans = FontFamily.SansSerif
    val primary = StravaTheme.colors.foregrounds.primary

    return Typography(
        headlines = Typography.Headlines(
            display = TextStyle(
                fontFamily = sans,
                fontWeight = FontWeight.W800,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                color = primary,
            ),
            title = TextStyle(
                fontFamily = sans,
                fontWeight = FontWeight.W700,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                color = primary,
            ),
            subtitle = TextStyle(
                fontFamily = sans,
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                color = primary,
            ),
        ),
        paragraphs = Typography.Paragraphs(
            body = TextStyle(
                fontFamily = sans,
                fontWeight = FontWeight.W400,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = primary,
            ),
        ),
        labels = Typography.Labels(
            large = TextStyle(
                fontFamily = sans,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                color = primary,
            ),
            small = TextStyle(
                fontFamily = sans,
                fontWeight = FontWeight.W500,
                fontSize = 10.sp,
                color = primary,
            ),
        ),
    )
}
