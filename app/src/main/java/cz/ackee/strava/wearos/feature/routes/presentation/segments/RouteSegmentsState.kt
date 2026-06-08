package cz.ackee.strava.wearos.feature.routes.presentation.segments

import cz.ackee.strava.wearos.feature.routes.domain.model.HighlightedSegment

sealed interface RouteSegmentsState {
    data object Loading : RouteSegmentsState

    data class Content(val segments: List<HighlightedSegment>) : RouteSegmentsState

    data object Error : RouteSegmentsState
}
