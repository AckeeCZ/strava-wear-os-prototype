package cz.ackee.strava.wearos.feature.routes.data.repository

import android.content.Context
import cz.ackee.strava.wearos.feature.routes.data.dto.StarredSegmentDto
import cz.ackee.strava.wearos.feature.routes.data.mapper.RouteMapper
import cz.ackee.strava.wearos.feature.routes.domain.model.StarredSegment
import cz.ackee.strava.wearos.feature.routes.domain.repository.StarredSegmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

class MockStarredSegmentRepository(
    private val context: Context,
    private val mapper: RouteMapper,
) : StarredSegmentRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Volatile
    private var cache: List<StarredSegment>? = null

    override suspend fun list(): List<StarredSegment> {
        delay(SIMULATED_NETWORK_DELAY)
        return cache ?: withContext(Dispatchers.IO) {
            val raw = context.assets.open(STARRED_SEGMENTS_ASSET).use { stream ->
                stream.bufferedReader().readText()
            }
            val starred = json.decodeFromString<List<StarredSegmentDto>>(raw)
                .map { mapper.map(it) }
            cache = starred
            starred
        }
    }

    private companion object {
        const val STARRED_SEGMENTS_ASSET = "routes/starred_segments.json"
        val SIMULATED_NETWORK_DELAY = 100.milliseconds
    }
}
