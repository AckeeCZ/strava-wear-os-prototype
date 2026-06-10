package cz.ackee.strava.wearos.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.presentation.detail.RouteDetailDestination
import cz.ackee.strava.wearos.feature.routes.presentation.detail.RouteDetailNavigation
import cz.ackee.strava.wearos.feature.routes.presentation.detail.RouteDetailScreen
import cz.ackee.strava.wearos.feature.routes.presentation.list.RoutesListDestination
import cz.ackee.strava.wearos.feature.routes.presentation.list.RoutesListScreen
import cz.ackee.strava.wearos.feature.routes.presentation.map.RouteMapDestination
import cz.ackee.strava.wearos.feature.routes.presentation.map.RouteMapScreen
import cz.ackee.strava.wearos.feature.routes.presentation.segments.RouteSegmentsDestination
import cz.ackee.strava.wearos.feature.routes.presentation.segments.RouteSegmentsScreen

@Composable
fun StravaNavDisplay() {
    val backStack = rememberNavBackStack(RoutesListDestination)
    val strategy = rememberSwipeDismissableSceneStrategy<NavKey>()
    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(strategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<RoutesListDestination> {
                RoutesListScreen(
                    onRouteClick = { routeId -> backStack.add(RouteDetailDestination(routeId)) },
                )
            }
            entry<RouteDetailDestination> { key ->
                RouteDetailScreen(
                    routeId = Route.Id(key.routeId),
                    navigation = RouteDetailNavigation(
                        onShowMap = { backStack.add(RouteMapDestination(key.routeId)) },
                        onShowAllSegments = { backStack.add(RouteSegmentsDestination(key.routeId)) },
                    ),
                )
            }
            entry<RouteMapDestination> { key ->
                RouteMapScreen(
                    routeId = Route.Id(key.routeId),
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<RouteSegmentsDestination> { key ->
                RouteSegmentsScreen(
                    routeId = Route.Id(key.routeId),
                )
            }
        },
    )
}
