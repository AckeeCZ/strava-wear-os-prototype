package cz.ackee.strava.wearos.feature.routes.data.mapper

import com.google.maps.android.SphericalUtil
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class RouteMapperTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private val mapper = RouteMapper()

    @Test
    fun `maps route 1 fixture into Route with decoded polyline and empty segments`() {
        val route = mapper.map(decode("routes/1.json"))

        assertEquals(1L, route.id.value)
        assertEquals("Oakland Tunnel Climb", route.name)
        assertTrue("distance must be positive", route.distanceMeters > 0.0)
        assertTrue(
            "polyline must decode to many points (Strava-encoded sample)",
            route.polyline.size > MIN_DECODED_POINTS,
        )
        assertTrue("route 1 has no segments in fixture", route.segments.isEmpty())
    }

    @Test
    fun `maps route 2 fixture and orders segments by route position with every polyline having at least two points`() {
        val route = mapper.map(decode("routes/2.json"))

        assertTrue("route 2 must have many segments", route.segments.size > MIN_ROUTE_2_SEGMENTS)
        route.segments.forEach { segment ->
            assertTrue("segment polyline must have at least 2 points", segment.polyline.size >= 2)
        }
        val distanceFromStartFirst = SphericalUtil.computeDistanceBetween(
            route.polyline.first(),
            route.segments.first().startLatLng,
        )
        val distanceFromStartLast = SphericalUtil.computeDistanceBetween(
            route.polyline.first(),
            route.segments.last().startLatLng,
        )
        assertTrue(
            "first segment must be closer to route start than the last segment",
            distanceFromStartFirst < distanceFromStartLast,
        )
    }

    @Test
    fun `maps route 3 fixture preserving segment lat lng parsed from array form`() {
        val route = mapper.map(decode("routes/3.json"))

        val segment = route.segments.single()
        assertEquals("Euro Spin Sprint", segment.name)
        assertNotNull(segment.startLatLng)
        assertNotNull(segment.endLatLng)
    }

    @Test
    fun `maps route 4 fixture containing categorized climbs`() {
        val route = mapper.map(decode("routes/4.json"))

        assertTrue("route 4 must have many segments", route.segments.size >= MIN_ROUTE_4_SEGMENTS)
        assertTrue(
            "route 4 should contain at least one categorized climb",
            route.segments.any { it.climbCategory > 0 },
        )
    }

    @Test
    fun `prefers detailed map polyline over summary polyline when both are present`() {
        val detailed = latLngs(50.0 to 14.0, 50.001 to 14.0, 50.002 to 14.0, 50.003 to 14.0)
        val summary = latLngs(50.0 to 14.0, 50.003 to 14.0)
        val dto = routeDto(polyline = summary, detailedPolyline = detailed)

        val route = mapper.map(dto)

        assertEquals(detailed.size, route.polyline.size)
    }

    @Test
    fun `falls back to summary polyline when detailed map polyline is null`() {
        val summary = latLngs(50.0 to 14.0, 50.001 to 14.0, 50.002 to 14.0)
        val dto = routeDto(polyline = summary, detailedPolyline = null)

        val route = mapper.map(dto)

        assertEquals(summary.size, route.polyline.size)
    }

    @Test
    fun `converts estimatedMovingTime seconds to Duration`() {
        val dto = routeDto(
            polyline = latLngs(50.0 to 14.0, 50.001 to 14.0),
            estimatedMovingTime = ESTIMATED_MOVING_TIME_SECONDS,
        )

        val route = mapper.map(dto)

        assertEquals(ESTIMATED_MOVING_TIME_SECONDS.seconds, route.estimatedMovingTime)
    }

    private fun decode(path: String): RouteDto =
        json.decodeFromString<RouteDto>(loadAsset(path))

    private companion object {
        const val MIN_DECODED_POINTS = 100
        const val MIN_ROUTE_2_SEGMENTS = 40
        const val MIN_ROUTE_4_SEGMENTS = 30
        const val ESTIMATED_MOVING_TIME_SECONDS = 5400L
    }
}
