package cz.ackee.strava.wearos.feature.routes.data.mapper

import com.google.android.gms.maps.model.LatLng
import cz.ackee.strava.wearos.feature.routes.data.dto.PolylineMapDto
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteDto
import org.junit.Assert.assertEquals
import org.junit.Test

class RouteMapperSegmentPolylineTest {

    private val mapper = RouteMapper()

    @Test
    fun `segment polyline is the route subrange between start and end indices when both lie on path`() {
        val polyline = linearPolyline()
        val segmentDto = routeSegmentDto(
            startLatLng = midpointBetween(polyline[1], polyline[2]),
            endLatLng = midpointBetween(polyline[3], polyline[4]),
        )

        val route = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))

        assertEquals(route.polyline.subList(1, 4), route.segments.single().polyline)
    }

    // KNOWN LIMITATION: a segment whose startLatLng is later along the route than its endLatLng
    // still receives a route-ascending polyline. Direction of travel is lost on reverse segments.
    @Test
    fun `segment polyline reuses ascending route subrange when start lies after end on route`() {
        val polyline = linearPolyline()
        val segmentDto = routeSegmentDto(
            startLatLng = midpointBetween(polyline[3], polyline[4]),
            endLatLng = midpointBetween(polyline[1], polyline[2]),
        )

        val route = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))

        assertEquals(route.polyline.subList(1, 4), route.segments.single().polyline)
    }

    @Test
    fun `segment polyline falls back to start-end pair when route polyline is empty`() {
        val start = LatLng(50.0015, 14.0)
        val end = LatLng(50.0035, 14.0)
        val dto = RouteDto(
            id = 1L,
            name = "Route",
            distance = 1000.0,
            elevationGain = 50.0,
            estimatedMovingTime = 3600L,
            map = PolylineMapDto(polyline = null, summaryPolyline = ""),
            segments = listOf(routeSegmentDto(startLatLng = start, endLatLng = end)),
        )

        val segment = mapper.map(dto).segments.single()

        assertEquals(listOf(start, end), segment.polyline)
    }

    @Test
    fun `segment polyline falls back to start-end pair when route polyline has single vertex`() {
        val polyline = listOf(LatLng(50.0, 14.0))
        val start = LatLng(50.0015, 14.0)
        val end = LatLng(50.0035, 14.0)

        val segment = mapper.map(
            routeDto(polyline = polyline, segments = listOf(routeSegmentDto(startLatLng = start, endLatLng = end))),
        ).segments.single()

        assertEquals(listOf(start, end), segment.polyline)
    }

    @Test
    fun `segment polyline falls back to start-end pair when start and end locate to the same route vertex`() {
        val polyline = linearPolyline()
        val coincident = midpointBetween(polyline[2], polyline[3])
        val segmentDto = routeSegmentDto(startLatLng = coincident, endLatLng = coincident)

        val segment = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))
            .segments.single()

        assertEquals(listOf(coincident, coincident), segment.polyline)
    }

    @Test
    fun `segment polyline picks nearest vertex range when both endpoints are far off path`() {
        val polyline = linearPolyline()
        val start = LatLng(50.0010, 14.01)
        val end = LatLng(50.0040, 14.01)
        val segmentDto = routeSegmentDto(startLatLng = start, endLatLng = end)

        val route = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))

        assertEquals(route.polyline.subList(1, 5), route.segments.single().polyline)
    }

    @Test
    fun `segment polyline picks on-path range when endpoint is within 50m tolerance`() {
        val polyline = linearPolyline()
        val start = LatLng(50.0015, 14.00042)
        val end = LatLng(50.0035, 14.00042)
        val segmentDto = routeSegmentDto(startLatLng = start, endLatLng = end)

        val route = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))

        assertEquals(route.polyline.subList(1, 4), route.segments.single().polyline)
    }

    @Test
    fun `segments are returned in route order even when DTO lists them out of order`() {
        val polyline = linearPolyline()
        val laterSegment = routeSegmentDto(
            id = 10L,
            startLatLng = midpointBetween(polyline[3], polyline[4]),
            endLatLng = midpointBetween(polyline[4], polyline[5]),
        )
        val earlierSegment = routeSegmentDto(
            id = 20L,
            startLatLng = midpointBetween(polyline[1], polyline[2]),
            endLatLng = midpointBetween(polyline[2], polyline[3]),
        )

        val ids = mapper.map(routeDto(polyline = polyline, segments = listOf(laterSegment, earlierSegment)))
            .segments.map { it.id.value }

        assertEquals(listOf(20L, 10L), ids)
    }

    @Test
    fun `reverse segments are sorted by where their range begins on the route not by DTO start position`() {
        val polyline = linearPolyline()
        val forwardSegment = routeSegmentDto(
            id = 100L,
            startLatLng = midpointBetween(polyline[2], polyline[3]),
            endLatLng = midpointBetween(polyline[3], polyline[4]),
        )
        // Reverse segment: startLatLng lies *later* along the route than endLatLng.
        val reverseSegment = routeSegmentDto(
            id = 200L,
            startLatLng = midpointBetween(polyline[3], polyline[4]),
            endLatLng = midpointBetween(polyline[1], polyline[2]),
        )

        val ids = mapper.map(routeDto(polyline = polyline, segments = listOf(forwardSegment, reverseSegment)))
            .segments.map { it.id.value }

        // Reverse segment first — its range begins at index 1 (forward starts at 2).
        assertEquals(listOf(200L, 100L), ids)
    }

    private fun linearPolyline(): List<LatLng> = latLngs(
        50.0000 to 14.0,
        50.0010 to 14.0,
        50.0020 to 14.0,
        50.0030 to 14.0,
        50.0040 to 14.0,
        50.0050 to 14.0,
    )

    private fun midpointBetween(a: LatLng, b: LatLng): LatLng =
        LatLng((a.latitude + b.latitude) / 2.0, (a.longitude + b.longitude) / 2.0)
}
