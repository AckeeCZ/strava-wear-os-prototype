package cz.ackee.strava.wearos.feature.routes.domain.model

import kotlin.time.Duration

data class StarredSegment(
    val segmentId: Segment.Id,
    val prTime: Duration?,
)
