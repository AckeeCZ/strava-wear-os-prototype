package cz.ackee.strava.wearos.feature.routes.data.mapper

import cz.ackee.strava.wearos.feature.routes.data.dto.RouteDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `decodes bundled route fixture into RouteDto and maps to domain`() {
        val raw = javaClass.classLoader!!
            .getResourceAsStream("routes/1.json")!!
            .bufferedReader()
            .readText()

        val dto = json.decodeFromString<RouteDto>(raw)
        val route = dto.toDomain()

        assertEquals(1L, route.id.value)
        assertEquals("Oakland Tunnel Climb", route.name)
        assertTrue("distance must be positive", route.distanceMeters > 0.0)
        assertTrue(
            "polyline must decode to many points (Strava-encoded sample)",
            route.polyline.size > MIN_DECODED_POINTS,
        )
    }

    private companion object {
        const val MIN_DECODED_POINTS = 100
    }
}
