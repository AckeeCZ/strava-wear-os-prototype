package cz.ackee.strava.wearos.feature.routes.data.mapper

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RouteMapperSegmentFieldsTest {

    private val mapper = RouteMapper()

    @Test
    fun `passes through id name distance grades elevations and climbCategory unchanged`() {
        val polyline = linearPolyline()
        val segmentDto = routeSegmentDto(
            id = 42L,
            name = "Hard Climb",
            distance = 1234.5,
            averageGrade = 5.5,
            maximumGrade = 12.0,
            elevationHigh = 350.0,
            elevationLow = 100.0,
            climbCategory = 3,
            startLatLng = polyline[1],
            endLatLng = polyline[3],
        )

        val segment = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))
            .segments.single()

        assertEquals(42L, segment.id.value)
        assertEquals("Hard Climb", segment.name)
        assertEquals(1234.5, segment.distanceMeters, 0.0)
        assertEquals(5.5, segment.gradeAverage, 0.0)
        assertEquals(12.0, segment.gradeMaximum, 0.0)
        assertEquals(350.0, segment.elevationHighMeters, 0.0)
        assertEquals(100.0, segment.elevationLowMeters, 0.0)
        assertEquals(3, segment.climbCategory)
    }

    @Test
    fun `passes through startLatLng and endLatLng identity`() {
        val polyline = linearPolyline()
        val start = LatLng(50.0015, 14.0)
        val end = LatLng(50.0035, 14.0)
        val segmentDto = routeSegmentDto(startLatLng = start, endLatLng = end)

        val segment = mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))
            .segments.single()

        assertEquals(start, segment.startLatLng)
        assertEquals(end, segment.endLatLng)
    }

    @Test
    fun `location is null when both city and country are null`() {
        assertNull(mapSingleSegmentLocation(city = null, country = null))
    }

    @Test
    fun `location is just city when country is null`() {
        assertEquals("Praha", mapSingleSegmentLocation(city = "Praha", country = null))
    }

    @Test
    fun `location is just country when city is null`() {
        assertEquals("Czechia", mapSingleSegmentLocation(city = null, country = "Czechia"))
    }

    @Test
    fun `location joins city and country with comma`() {
        assertEquals("Praha, Czechia", mapSingleSegmentLocation(city = "Praha", country = "Czechia"))
    }

    @Test
    fun `location is null when city and country are both blank strings`() {
        assertNull(mapSingleSegmentLocation(city = "", country = "   "))
    }

    @Test
    fun `location skips blank city and keeps country`() {
        assertEquals("Czechia", mapSingleSegmentLocation(city = "", country = "Czechia"))
    }

    @Test
    fun `location skips blank country and keeps city`() {
        assertEquals("Praha", mapSingleSegmentLocation(city = "Praha", country = "   "))
    }

    private fun mapSingleSegmentLocation(city: String?, country: String?): String? {
        val polyline = linearPolyline()
        val segmentDto = routeSegmentDto(
            startLatLng = polyline[1],
            endLatLng = polyline[3],
            city = city,
            country = country,
        )
        return mapper.map(routeDto(polyline = polyline, segments = listOf(segmentDto)))
            .segments.single()
            .location
    }

    private fun linearPolyline(): List<LatLng> = latLngs(
        50.0000 to 14.0,
        50.0010 to 14.0,
        50.0020 to 14.0,
        50.0030 to 14.0,
        50.0040 to 14.0,
        50.0050 to 14.0,
    )
}
