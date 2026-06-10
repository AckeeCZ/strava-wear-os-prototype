package cz.ackee.strava.wearos.feature.routes.domain.usecase

import cz.ackee.strava.wearos.feature.routes.domain.model.HighlightedSegment
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.repository.RouteRepository
import cz.ackee.strava.wearos.feature.routes.domain.repository.StarredSegmentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetRouteWithHighlightedSegmentsUseCase(
    private val routeRepository: RouteRepository,
    private val starredSegmentRepository: StarredSegmentRepository,
) {

    suspend operator fun invoke(id: Route.Id): RouteWithHighlights = coroutineScope {
        val routeAsync = async { routeRepository.get(id) }
        val starredAsync = async { starredSegmentRepository.list() }
        val route = routeAsync.await()
        val starredById = starredAsync.await().associateBy { it.segmentId }
        val segments = route.segments.map { segment ->
            val starred = starredById[segment.id]
            HighlightedSegment(
                segment = segment,
                prTime = starred?.prTime,
                isStarred = starred != null,
            )
        }
        RouteWithHighlights(route, segments)
    }
}

data class RouteWithHighlights(
    val route: Route,
    val segments: List<HighlightedSegment>,
)
