package cz.ackee.strava.wearos.feature.routes.domain.model

import kotlin.time.Duration

data class HighlightedSegment(
    val segment: Segment,
    val prTime: Duration?,
    val isStarred: Boolean,
)
