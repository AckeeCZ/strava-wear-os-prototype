package cz.ackee.strava.wearos.feature.routes.presentation.map

import cz.ackee.strava.wearos.feature.routes.domain.model.Segment

sealed interface SegmentMapState {
    data object Loading : SegmentMapState
    data class Content(val segment: Segment) : SegmentMapState
    data object Error : SegmentMapState
}
