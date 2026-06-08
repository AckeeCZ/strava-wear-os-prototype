package cz.ackee.strava.wearos.feature.routes.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.google.android.gms.maps.model.LatLng
import cz.ackee.strava.wearos.R
import cz.ackee.strava.wearos.core.presentation.map.rememberMapStyle
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme
import cz.ackee.strava.wearos.feature.routes.domain.model.HighlightedSegment
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.model.Segment
import cz.ackee.strava.wearos.feature.routes.presentation.components.MetricRow
import cz.ackee.strava.wearos.feature.routes.presentation.components.SegmentCard
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RouteDetailScreen(
    routeId: Route.Id,
    navigation: RouteDetailNavigation,
    viewModel: RouteDetailViewModel = koinViewModel { parametersOf(routeId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RouteDetailScreen(
        state = state,
        onShowMap = navigation.onShowMap,
        onShowAllSegments = navigation.onShowAllSegments,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun RouteDetailScreen(
    state: RouteDetailState,
    onShowMap: () -> Unit,
    onShowAllSegments: () -> Unit,
    onIntent: (RouteDetailIntent) -> Unit,
) {
    when (state) {
        RouteDetailState.Loading -> LoadingState()
        RouteDetailState.Error -> ErrorState(onRetry = { onIntent(RouteDetailIntent.Retry) })
        is RouteDetailState.Content -> ContentState(
            state = state,
            onShowMap = onShowMap,
            onShowAllSegments = onShowAllSegments,
        )
    }
}

@Composable
private fun LoadingState() {
    ScreenScaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                colors = ProgressIndicatorDefaults.colors(
                    indicatorColor = StravaTheme.colors.accent,
                    trackColor = StravaTheme.colors.backgrounds.surfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    ScreenScaffold {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.route_detail_error),
                    style = StravaTheme.typography.paragraphs.body,
                    color = StravaTheme.colors.foregrounds.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            EdgeButton(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StravaTheme.colors.accent,
                    contentColor = StravaTheme.colors.onAccent,
                ),
            ) {
                Text(
                    text = stringResource(R.string.route_detail_retry),
                    style = StravaTheme.typography.labels.large,
                )
            }
        }
    }
}

@Composable
private fun ContentState(
    state: RouteDetailState.Content,
    onShowMap: () -> Unit,
    onShowAllSegments: () -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            if (state.hasMoreSegments) {
                EdgeButton(
                    onClick = onShowAllSegments,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StravaTheme.colors.backgrounds.surface,
                        contentColor = StravaTheme.colors.foregrounds.primary,
                    ),
                ) {
                    Text(
                        text = stringResource(
                            R.string.route_detail_segments_show_all,
                            state.totalSegments,
                        ),
                        style = StravaTheme.typography.labels.large,
                    )
                }
            }
        },
    ) { contentPadding ->
        DetailContent(
            state = state,
            onShowMap = onShowMap,
            listState = listState,
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun DetailContent(
    state: RouteDetailState.Content,
    onShowMap: () -> Unit,
    listState: TransformingLazyColumnState,
    contentPadding: PaddingValues,
) {
    val spec = rememberTransformationSpec()
    val segmentMapStyle = rememberMapStyle(R.raw.map_style_dark_minimal)
    TransformingLazyColumn(
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            Header(
                title = state.route.name,
                style = StravaTheme.typography.headlines.title,
                maxLines = 2,
                modifier = Modifier.transformedHeight(this, spec),
                transformation = SurfaceTransformation(spec),
            )
        }
        item {
            StatsCard(
                route = state.route,
                onShowMap = onShowMap,
                modifier = Modifier.fillMaxWidth().transformedHeight(this, spec),
                transformation = SurfaceTransformation(spec),
            )
        }
        if (state.highlightedSegments.isNotEmpty()) {
            item {
                Header(
                    title = stringResource(R.string.route_detail_segments_header),
                    style = StravaTheme.typography.headlines.subtitle,
                    modifier = Modifier.transformedHeight(this, spec),
                    transformation = SurfaceTransformation(spec),
                )
            }
            items(state.highlightedSegments, key = { it.segment.id.value }) { highlighted ->
                SegmentCard(
                    highlighted = highlighted,
                    mapStyle = segmentMapStyle,
                    modifier = Modifier.fillMaxWidth().transformedHeight(this@items, spec),
                    transformation = SurfaceTransformation(spec),
                )
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    ListHeader(modifier = modifier, transformation = transformation) {
        Text(
            text = title,
            style = style,
            color = StravaTheme.colors.foregrounds.primary,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StatsCard(
    route: Route,
    onShowMap: () -> Unit,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
) {
    Card(
        onClick = {},
        modifier = modifier,
        transformation = transformation,
        colors = CardDefaults.cardColors(
            containerColor = StravaTheme.colors.backgrounds.surface,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RouteMetricsColumn(route = route, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = onShowMap,
                modifier = Modifier.size(PLAY_BUTTON_SIZE),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = StravaTheme.colors.accent,
                    contentColor = StravaTheme.colors.onAccent,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_map),
                    contentDescription = stringResource(R.string.route_detail_show_on_map),
                )
            }
        }
    }
}

@Composable
private fun RouteMetricsColumn(route: Route, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        MetricRow(
            iconRes = R.drawable.ic_distance,
            text = stringResource(R.string.route_detail_distance, route.distanceMeters / METERS_PER_KILOMETER),
            color = StravaTheme.colors.foregrounds.primary,
        )
        MetricRow(
            iconRes = R.drawable.ic_elevation,
            text = stringResource(R.string.route_detail_elevation, route.elevationGainMeters.roundToInt()),
            color = StravaTheme.colors.foregrounds.secondary,
            modifier = Modifier.padding(top = 2.dp),
        )
        route.estimatedMovingTime
            .takeIf { it > Duration.ZERO }
            ?.let { duration ->
                MetricRow(
                    iconRes = R.drawable.ic_time,
                    text = stringResource(
                        R.string.route_detail_time_hm,
                        duration.inWholeHours.toInt(),
                        (duration.inWholeMinutes % MINUTES_PER_HOUR).toInt(),
                    ),
                    color = StravaTheme.colors.foregrounds.secondary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
    }
}

private const val METERS_PER_KILOMETER = 1000.0
private const val MINUTES_PER_HOUR = 60L
private val PLAY_BUTTON_SIZE = 40.dp

@Suppress("MagicNumber")
private class RouteDetailStateProvider : PreviewParameterProvider<RouteDetailState> {
    private fun segment(id: Long, name: String, distance: Double): Segment = Segment(
        id = Segment.Id(id),
        name = name,
        distanceMeters = distance,
        gradeAverage = 2.5,
        gradeMaximum = 7.0,
        elevationHighMeters = 12.0,
        elevationLowMeters = 0.0,
        climbCategory = 0,
        location = "Praha",
        startLatLng = LatLng(0.0, 0.0),
        endLatLng = LatLng(0.0, 0.0),
        polyline = emptyList(),
    )

    override val values = sequenceOf(
        RouteDetailState.Loading,
        RouteDetailState.Error,
        RouteDetailState.Content(
            route = Route(
                id = Route.Id(1L),
                name = "Pražský Průhon",
                distanceMeters = 15_200.0,
                elevationGainMeters = 182.0,
                estimatedMovingTime = 1.hours + 5.minutes,
                polyline = emptyList(),
                segments = emptyList(),
            ),
            highlightedSegments = listOf(
                HighlightedSegment(
                    segment = segment(101L, "Vyšehradský sjezd", 850.0),
                    prTime = 90.seconds,
                    isStarred = true,
                ),
                HighlightedSegment(
                    segment = segment(102L, "Smíchovský most", 420.0),
                    prTime = null,
                    isStarred = false,
                ),
            ),
            totalSegments = 5,
        ),
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RouteDetailScreenPreview(
    @PreviewParameter(RouteDetailStateProvider::class) state: RouteDetailState,
) {
    StravaTheme {
        RouteDetailScreen(
            state = state,
            onShowMap = {},
            onShowAllSegments = {},
            onIntent = {},
        )
    }
}
