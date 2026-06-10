package cz.ackee.strava.wearos.feature.routes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import cz.ackee.strava.wearos.R
import cz.ackee.strava.wearos.core.presentation.map.WearMapStatic
import cz.ackee.strava.wearos.core.presentation.map.drawPolyline
import cz.ackee.strava.wearos.core.presentation.map.fitCameraToPolyline
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme
import cz.ackee.strava.wearos.feature.routes.domain.model.HighlightedSegment
import kotlin.time.Duration

@Composable
fun SegmentCard(
    highlighted: HighlightedSegment,
    mapStyle: MapStyleOptions,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
) {
    val segment = highlighted.segment
    Card(
        onClick = onClick,
        modifier = modifier,
        transformation = transformation,
        colors = CardDefaults.cardColors(
            containerColor = StravaTheme.colors.backgrounds.surface,
        ),
    ) {
        Column {
            Text(
                text = segment.name,
                style = StravaTheme.typography.headlines.subtitle,
                color = StravaTheme.colors.foregrounds.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (segment.polyline.size >= MIN_POLYLINE_POINTS) {
                    SegmentMiniMap(
                        polyline = segment.polyline,
                        mapStyle = mapStyle,
                        modifier = Modifier.size(width = MINIMAP_WIDTH, height = MINIMAP_HEIGHT),
                    )
                    Spacer(Modifier.width(10.dp))
                }
                SegmentMetrics(
                    distanceMeters = segment.distanceMeters,
                    elevationGainMeters = segment.elevationGainMeters,
                    modifier = Modifier.weight(1f),
                )
                highlighted.prTime?.let { prTime ->
                    Spacer(Modifier.width(6.dp))
                    PrBadge(prTime = prTime)
                }
            }
        }
    }
}

@Composable
private fun SegmentMiniMap(polyline: List<LatLng>, mapStyle: MapStyleOptions, modifier: Modifier = Modifier) {
    val polylineColor = StravaTheme.colors.map.polyline.toArgb()
    val placeholderColor = StravaTheme.colors.backgrounds.surface
    val density = LocalDensity.current
    WearMapStatic(
        mapStyle = mapStyle,
        placeholderColor = placeholderColor,
        cornerRadius = MINIMAP_CORNER_RADIUS,
        modifier = modifier,
        configureMap = { onReady ->
            clear()
            drawPolyline(
                polyline = polyline,
                colorArgb = polylineColor,
                widthPx = with(density) { POLYLINE_WIDTH.toPx() },
            )
            fitCameraToPolyline(
                polyline = polyline,
                cameraPaddingPx = with(density) { CAMERA_PADDING.roundToPx() },
                onReady = onReady,
            )
        },
    )
}

@Composable
private fun SegmentMetrics(distanceMeters: Double, elevationGainMeters: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        MetricRow(
            iconRes = R.drawable.ic_distance,
            text = stringResource(R.string.route_detail_distance, distanceMeters / METERS_PER_KILOMETER),
            color = StravaTheme.colors.foregrounds.secondary,
        )
        MetricRow(
            iconRes = R.drawable.ic_elevation,
            text = stringResource(R.string.route_detail_elevation, elevationGainMeters),
            color = StravaTheme.colors.foregrounds.secondary,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun PrBadge(prTime: Duration, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(StravaTheme.colors.accent)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = prTime.toMinutesSeconds(),
            style = StravaTheme.typography.labels.small,
            color = StravaTheme.colors.onAccent,
        )
    }
}

private fun Duration.toMinutesSeconds(): String {
    val totalSeconds = inWholeSeconds
    val minutes = totalSeconds / SECONDS_PER_MINUTE
    val seconds = (totalSeconds % SECONDS_PER_MINUTE).toInt()
    return "%d:%02d".format(minutes, seconds)
}

private val MINIMAP_WIDTH = 36.dp
private val MINIMAP_HEIGHT = 28.dp
private val MINIMAP_CORNER_RADIUS = 6.dp
private const val METERS_PER_KILOMETER = 1000.0
private const val SECONDS_PER_MINUTE = 60L
private const val MIN_POLYLINE_POINTS = 2
private val POLYLINE_WIDTH = 2.dp
private val CAMERA_PADDING = 4.dp
