package cz.ackee.strava.wearos.feature.routes.presentation.detail

data class RouteDetailNavigation(
    val onShowMap: () -> Unit,
    val onShowAllSegments: () -> Unit,
)
