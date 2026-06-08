package cz.ackee.strava.wearos.feature.routes.presentation.segments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import cz.ackee.strava.wearos.R
import cz.ackee.strava.wearos.core.presentation.map.rememberMapStyle
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.presentation.components.SegmentCard
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RouteSegmentsScreen(
    routeId: Route.Id,
    viewModel: RouteSegmentsViewModel = koinViewModel { parametersOf(routeId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RouteSegmentsScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun RouteSegmentsScreen(
    state: RouteSegmentsState,
    onIntent: (RouteSegmentsIntent) -> Unit,
) {
    when (state) {
        RouteSegmentsState.Loading -> LoadingState()
        RouteSegmentsState.Error -> ErrorState(onRetry = { onIntent(RouteSegmentsIntent.Retry) })
        is RouteSegmentsState.Content -> ContentState(state = state)
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
                    text = stringResource(R.string.route_segments_error),
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
                    text = stringResource(R.string.route_segments_retry),
                    style = StravaTheme.typography.labels.large,
                )
            }
        }
    }
}

@Composable
private fun ContentState(state: RouteSegmentsState.Content) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()
    val segmentMapStyle = rememberMapStyle(R.raw.map_style_dark_minimal)
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item {
                ListHeader(
                    modifier = Modifier.transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) {
                    Text(
                        text = stringResource(R.string.route_segments_title, state.segments.size),
                        style = StravaTheme.typography.headlines.title,
                        color = StravaTheme.colors.foregrounds.primary,
                    )
                }
            }
            items(state.segments, key = { it.segment.id.value }) { highlighted ->
                SegmentCard(
                    highlighted = highlighted,
                    mapStyle = segmentMapStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this@items, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                )
            }
        }
    }
}

private class RouteSegmentsStateProvider : PreviewParameterProvider<RouteSegmentsState> {
    override val values = sequenceOf(
        RouteSegmentsState.Loading,
        RouteSegmentsState.Error,
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RouteSegmentsScreenPreview(
    @PreviewParameter(RouteSegmentsStateProvider::class) state: RouteSegmentsState,
) {
    StravaTheme {
        RouteSegmentsScreen(state = state, onIntent = {})
    }
}
