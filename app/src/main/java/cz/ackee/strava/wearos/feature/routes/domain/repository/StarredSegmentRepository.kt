package cz.ackee.strava.wearos.feature.routes.domain.repository

import cz.ackee.strava.wearos.feature.routes.domain.model.StarredSegment

interface StarredSegmentRepository {

    suspend fun list(): List<StarredSegment>
}
