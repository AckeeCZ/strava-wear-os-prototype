package cz.ackee.strava.wearos.feature.routes.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme

@Composable
fun MetricRow(
    @DrawableRes iconRes: Int,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val fontSize = with(LocalDensity.current) { METRIC_ICON_SIZE.toSp() }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(METRIC_ICON_SIZE),
        )
        Spacer(Modifier.width(METRIC_ROW_GAP))
        Text(
            text = text,
            style = StravaTheme.typography.labels.large.copy(fontSize = fontSize),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val METRIC_ICON_SIZE = 12.dp
private val METRIC_ROW_GAP = 4.dp
