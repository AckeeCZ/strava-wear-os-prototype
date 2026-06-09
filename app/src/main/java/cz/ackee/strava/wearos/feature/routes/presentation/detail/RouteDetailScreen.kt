package cz.ackee.strava.wearos.feature.routes.presentation.detail

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import cz.ackee.strava.wearos.R
import cz.ackee.strava.wearos.core.presentation.map.WearMap
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RouteDetailScreen(
    routeId: Route.Id,
    onBack: () -> Unit,
    viewModel: RouteDetailViewModel = koinViewModel { parametersOf(routeId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RouteDetailScreen(state = state, onBack = onBack, onIntent = viewModel::onIntent)
}

@Composable
private fun RouteDetailScreen(
    state: RouteDetailState,
    onBack: () -> Unit,
    onIntent: (RouteDetailIntent) -> Unit,
) {
    when (state) {
        RouteDetailState.Loading -> LoadingState()
        RouteDetailState.Error -> ErrorState(onRetry = { onIntent(RouteDetailIntent.Retry) })
        is RouteDetailState.Content -> RouteMap(route = state.route, onBack = onBack)
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
private fun RouteMap(route: Route, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val polylineColor = StravaTheme.colors.map.polyline.toArgb()
    WearMap(
        onBack = onBack,
        modifier = modifier,
        configureMap = {
            clear()
            drawRoute(route.polyline, polylineColor)
        },
    )
}

private fun GoogleMap.drawRoute(polyline: List<LatLng>, colorArgb: Int) {
    if (polyline.isEmpty()) return
    addPolyline(
        PolylineOptions()
            .addAll(polyline)
            .color(colorArgb)
            .width(POLYLINE_WIDTH_PX),
    )
    val bounds = LatLngBounds.builder().apply {
        polyline.forEach { include(it) }
    }.build()
    moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_PADDING_PX))
}

private const val POLYLINE_WIDTH_PX = 8f
private const val CAMERA_PADDING_PX = 24

private class RouteDetailStateProvider : PreviewParameterProvider<RouteDetailState> {
    override val values = sequenceOf(
        RouteDetailState.Loading,
        RouteDetailState.Error,
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RouteDetailScreenPreview(
    @PreviewParameter(RouteDetailStateProvider::class) state: RouteDetailState,
) {
    StravaTheme {
        RouteDetailScreen(state = state, onBack = {}, onIntent = {})
    }
}
