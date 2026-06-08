package cz.ackee.strava.wearos.feature.routes.data.mapper

import cz.ackee.strava.wearos.feature.routes.data.dto.StarredSegmentDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class StarredSegmentMappingTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private val mapper = RouteMapper()

    @Test
    fun `decodes bundled starred segments and maps to domain`() {
        val raw = loadAsset("routes/starred_segments.json")

        val starred = json.decodeFromString<List<StarredSegmentDto>>(raw)
            .map { mapper.map(it) }

        assertTrue("starred list must not be empty", starred.isNotEmpty())
        val withPr = starred.single { it.segmentId.value == VYSEHRAD_SEGMENT_ID }
        assertEquals(VYSEHRAD_PR_SECONDS.seconds, withPr.prTime)
        val withoutPr = starred.single { it.segmentId.value == EURO_SPIN_SEGMENT_ID }
        assertNull("Run segment in fixture has no pr_time", withoutPr.prTime)
    }

    private companion object {
        const val VYSEHRAD_SEGMENT_ID = 2212335L
        const val VYSEHRAD_PR_SECONDS = 396L
        const val EURO_SPIN_SEGMENT_ID = 20369633L
    }
}
