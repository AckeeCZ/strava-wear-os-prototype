package cz.ackee.strava.wearos.feature.routes.data

import com.google.maps.android.PolyUtil
import cz.ackee.strava.wearos.feature.routes.domain.Route
import kotlin.time.Duration.Companion.seconds

fun RouteDto.toDomain(): Route = Route(
    id = Route.Id(id),
    name = name,
    distanceMeters = distance,
    elevationGainMeters = elevationGain,
    estimatedMovingTime = estimatedMovingTime.seconds,
    polyline = PolyUtil.decode(map.polyline ?: map.summaryPolyline),
)
