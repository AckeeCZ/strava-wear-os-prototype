package cz.ackee.strava.wearos.feature.routes.presentation.segments

sealed interface RouteSegmentsIntent {
    data object Retry : RouteSegmentsIntent
}
