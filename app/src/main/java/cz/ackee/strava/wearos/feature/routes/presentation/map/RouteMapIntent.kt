package cz.ackee.strava.wearos.feature.routes.presentation.map

sealed interface RouteMapIntent {
    data object Retry : RouteMapIntent
}
