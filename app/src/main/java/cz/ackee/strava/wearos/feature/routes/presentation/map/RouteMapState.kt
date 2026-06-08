package cz.ackee.strava.wearos.feature.routes.presentation.map

import cz.ackee.strava.wearos.feature.routes.domain.model.Route

sealed interface RouteMapState {
    data object Loading : RouteMapState
    data class Content(val route: Route) : RouteMapState
    data object Error : RouteMapState
}
