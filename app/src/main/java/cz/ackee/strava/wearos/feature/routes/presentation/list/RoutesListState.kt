package cz.ackee.strava.wearos.feature.routes.presentation.list

import cz.ackee.strava.wearos.feature.routes.domain.Route

sealed interface RoutesListState {

    data object Loading : RoutesListState

    data class Content(val routes: List<RouteSummary>) : RoutesListState

    data object Error : RoutesListState
}

data class RouteSummary(
    val id: Route.Id,
    val name: String,
    val distanceKm: Double,
    val elevationGainM: Int,
)
