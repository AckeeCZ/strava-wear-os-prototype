package cz.ackee.strava.wearos.feature.routes.presentation.map

sealed interface SegmentMapIntent {
    data object Retry : SegmentMapIntent
}
