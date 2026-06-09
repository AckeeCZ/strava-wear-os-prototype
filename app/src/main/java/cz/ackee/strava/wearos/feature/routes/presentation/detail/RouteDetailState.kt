package cz.ackee.strava.wearos.feature.routes.presentation.detail

import cz.ackee.strava.wearos.feature.routes.domain.model.Route

sealed interface RouteDetailState {
    data object Loading : RouteDetailState
    data class Content(val route: Route) : RouteDetailState
    data object Error : RouteDetailState
}
