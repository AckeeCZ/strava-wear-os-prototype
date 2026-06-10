package cz.ackee.strava.wearos.feature.routes.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ProgressIndicatorDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import cz.ackee.strava.wearos.R
import cz.ackee.strava.wearos.core.presentation.map.WearMap
import cz.ackee.strava.wearos.core.presentation.map.drawPolyline
import cz.ackee.strava.wearos.core.presentation.map.rememberMapStyle
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RouteMapScreen(
    routeId: Route.Id,
    onBack: () -> Unit,
    viewModel: RouteMapViewModel = koinViewModel { parametersOf(routeId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RouteMapScreen(state = state, onBack = onBack, onIntent = viewModel::onIntent)
}

@Composable
private fun RouteMapScreen(
    state: RouteMapState,
    onBack: () -> Unit,
    onIntent: (RouteMapIntent) -> Unit,
) {
    when (state) {
        RouteMapState.Loading -> LoadingState()
        RouteMapState.Error -> ErrorState(onRetry = { onIntent(RouteMapIntent.Retry) })
        is RouteMapState.Content -> RouteMap(route = state.route, onBack = onBack)
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
                    text = stringResource(R.string.route_map_error),
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
                    text = stringResource(R.string.route_map_retry),
                    style = StravaTheme.typography.labels.large,
                )
            }
        }
    }
}

@Composable
private fun RouteMap(route: Route, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val polylineColor = StravaTheme.colors.map.polyline.toArgb()
    val mapStyle = rememberMapStyle(R.raw.map_style_dark)
    val density = LocalDensity.current
    WearMap(
        onBack = onBack,
        mapStyle = mapStyle,
        modifier = modifier,
        configureMap = {
            clear()
            drawPolyline(
                polyline = route.polyline,
                colorArgb = polylineColor,
                widthPx = with(density) { POLYLINE_WIDTH.toPx() },
                cameraPaddingPx = with(density) { CAMERA_PADDING.roundToPx() },
            )
        },
    )
}

private class RouteMapStateProvider : PreviewParameterProvider<RouteMapState> {
    override val values = sequenceOf(
        RouteMapState.Loading,
        RouteMapState.Error,
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RouteMapScreenPreview(
    @PreviewParameter(RouteMapStateProvider::class) state: RouteMapState,
) {
    StravaTheme {
        RouteMapScreen(state = state, onBack = {}, onIntent = {})
    }
}

private val POLYLINE_WIDTH = 2.dp
private val CAMERA_PADDING = 12.dp
