package cz.ackee.strava.wearos.feature.routes.presentation.detail

import cz.ackee.strava.wearos.feature.routes.domain.model.HighlightedSegment
import cz.ackee.strava.wearos.feature.routes.domain.model.Route

sealed interface RouteDetailState {
    data object Loading : RouteDetailState

    data class Content(
        val route: Route,
        val highlightedSegments: List<HighlightedSegment>,
        val totalSegments: Int,
    ) : RouteDetailState {
        val hasMoreSegments: Boolean
            get() = highlightedSegments.size < totalSegments
    }

    data object Error : RouteDetailState
}
