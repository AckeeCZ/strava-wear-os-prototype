package cz.ackee.strava.wearos.feature.routes.presentation.detail

import cz.ackee.strava.wearos.feature.routes.domain.model.Segment

data class RouteDetailNavigation(
    val onShowMap: () -> Unit,
    val onShowAllSegments: () -> Unit,
    val onShowSegmentMap: (Segment.Id) -> Unit,
)
