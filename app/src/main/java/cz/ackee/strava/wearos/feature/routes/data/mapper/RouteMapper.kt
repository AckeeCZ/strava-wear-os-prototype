package cz.ackee.strava.wearos.feature.routes.data.mapper

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteDto
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteSegmentDto
import cz.ackee.strava.wearos.feature.routes.data.dto.StarredSegmentDto
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.model.Segment
import cz.ackee.strava.wearos.feature.routes.domain.model.StarredSegment
import kotlin.time.Duration.Companion.seconds

class RouteMapper {

    fun map(dto: RouteDto): Route {
        val routePolyline: List<LatLng> = PolyUtil.decode(dto.map.polyline ?: dto.map.summaryPolyline)
        val orderedSegments = dto.segments
            .map { mapSegment(it, routePolyline) }
            .sortedBy { it.routeIndex }
            .map { it.segment }
        return Route(
            id = Route.Id(dto.id),
            name = dto.name,
            distanceMeters = dto.distance,
            elevationGainMeters = dto.elevationGain,
            estimatedMovingTime = dto.estimatedMovingTime.seconds,
            polyline = routePolyline,
            segments = orderedSegments,
        )
    }

    fun map(dto: StarredSegmentDto): StarredSegment = StarredSegment(
        segmentId = Segment.Id(dto.id),
        prTime = dto.prTime?.seconds,
    )

    private fun mapSegment(dto: RouteSegmentDto, routePolyline: List<LatLng>): SegmentWithIndex {
        val startIndex = locatePointOnPath(dto.startLatLng, routePolyline)
        val endIndex = locatePointOnPath(dto.endLatLng, routePolyline)
        val rangeStart = minOf(startIndex, endIndex)
        val rangeEnd = (maxOf(startIndex, endIndex) + 1).coerceAtMost(routePolyline.size)
        val segmentPolyline = if (rangeStart in routePolyline.indices && rangeEnd - rangeStart >= 2) {
            routePolyline.subList(rangeStart, rangeEnd)
        } else {
            listOf(dto.startLatLng, dto.endLatLng)
        }
        return SegmentWithIndex(
            segment = Segment(
                id = Segment.Id(dto.id),
                name = dto.name,
                distanceMeters = dto.distance,
                gradeAverage = dto.averageGrade,
                gradeMaximum = dto.maximumGrade,
                elevationHighMeters = dto.elevationHigh,
                elevationLowMeters = dto.elevationLow,
                climbCategory = dto.climbCategory,
                location = listOfNotNull(dto.city, dto.country)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
                    .takeIf { it.isNotEmpty() },
                startLatLng = dto.startLatLng,
                endLatLng = dto.endLatLng,
                polyline = segmentPolyline,
            ),
            routeIndex = rangeStart,
        )
    }

    private fun locatePointOnPath(point: LatLng, polyline: List<LatLng>): Int {
        if (polyline.isEmpty()) return 0
        val indexOnPath = PolyUtil.locationIndexOnPath(point, polyline, true, ON_PATH_TOLERANCE_METERS)
        return if (indexOnPath >= 0) {
            indexOnPath
        } else {
            polyline.indices.minByOrNull {
                SphericalUtil.computeDistanceBetween(point, polyline[it])
            } ?: 0
        }
    }
}

private data class SegmentWithIndex(val segment: Segment, val routeIndex: Int)

private const val ON_PATH_TOLERANCE_METERS = 50.0
