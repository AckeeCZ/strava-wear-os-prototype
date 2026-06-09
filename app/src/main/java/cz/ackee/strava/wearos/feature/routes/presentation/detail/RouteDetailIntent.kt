package cz.ackee.strava.wearos.feature.routes.presentation.detail

sealed interface RouteDetailIntent {
    data object Retry : RouteDetailIntent
}
