package cz.ackee.strava.wearos.feature.routes.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
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
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import org.koin.androidx.compose.koinViewModel

@Composable
fun RoutesListScreen(
    onRouteClick: (routeId: Long) -> Unit,
    viewModel: RoutesListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RoutesListScreen(
        state = state,
        onRouteClick = onRouteClick,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun RoutesListScreen(
    state: RoutesListState,
    onRouteClick: (routeId: Long) -> Unit,
    onIntent: (RoutesListIntent) -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        when (state) {
            RoutesListState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is RoutesListState.Content -> ContentState(
                routes = state.routes,
                onRouteClick = onRouteClick,
                contentPadding = contentPadding,
                listState = listState,
            )
            RoutesListState.Error -> ErrorState(
                onRetry = { onIntent(RoutesListIntent.Retry) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun RetryEdgeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    EdgeButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = StravaTheme.colors.accent,
            contentColor = StravaTheme.colors.onAccent,
        ),
    ) {
        Text(
            text = stringResource(R.string.routes_list_retry),
            style = StravaTheme.typography.labels.large,
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            colors = ProgressIndicatorDefaults.colors(
                indicatorColor = StravaTheme.colors.accent,
                trackColor = StravaTheme.colors.backgrounds.surfaceVariant,
            ),
        )
    }
}

@Composable
private fun ContentState(
    routes: List<RouteSummary>,
    onRouteClick: (routeId: Long) -> Unit,
    contentPadding: PaddingValues,
    listState: TransformingLazyColumnState,
) {
    val transformationSpec = rememberTransformationSpec()
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
                    text = stringResource(R.string.routes_list_title),
                    style = StravaTheme.typography.headlines.title,
                    color = StravaTheme.colors.foregrounds.primary,
                )
            }
        }
        items(routes, key = { it.id.value }) { route ->
            RouteCard(
                route = route,
                onClick = { onRouteClick(route.id.value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .transformedHeight(this@items, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec),
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun RouteCard(
    route: RouteSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        transformation = transformation,
        colors = CardDefaults.cardColors(
            containerColor = StravaTheme.colors.backgrounds.surface,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = CardDefaults.Height - 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(StravaTheme.colors.accent),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = route.name,
                        style = StravaTheme.typography.headlines.subtitle,
                        color = StravaTheme.colors.foregrounds.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(
                            R.string.routes_list_route_metrics,
                            route.distanceKm,
                            route.elevationGainM,
                        ),
                        style = StravaTheme.typography.paragraphs.body,
                        color = StravaTheme.colors.foregrounds.secondary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = stringResource(R.string.routes_list_error),
            style = StravaTheme.typography.paragraphs.body,
            color = StravaTheme.colors.foregrounds.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp),
        )
        RetryEdgeButton(
            onClick = onRetry,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Suppress("MagicNumber")
private class RoutesListStateProvider : PreviewParameterProvider<RoutesListState> {
    override val values = sequenceOf(
        RoutesListState.Loading,
        RoutesListState.Content(
            routes = listOf(
                RouteSummary(Route.Id(1), "Pražský Průhon", 15.2, 182),
                RouteSummary(Route.Id(2), "Tatra Ridge Traverse", 27.8, 1783),
                RouteSummary(Route.Id(3), "Šumava Crossings", 21.9, 648),
            ),
        ),
        RoutesListState.Error,
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RoutesListScreenPreview(
    @PreviewParameter(RoutesListStateProvider::class) state: RoutesListState,
) {
    StravaTheme {
        RoutesListScreen(state = state, onRouteClick = {}, onIntent = {})
    }
}
