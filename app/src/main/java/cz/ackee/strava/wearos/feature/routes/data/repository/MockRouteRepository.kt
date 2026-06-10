package cz.ackee.strava.wearos.feature.routes.data.repository

import android.content.Context
import cz.ackee.strava.wearos.feature.routes.data.dto.RouteDto
import cz.ackee.strava.wearos.feature.routes.data.mapper.RouteMapper
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

class MockRouteRepository(
    private val context: Context,
    private val mapper: RouteMapper,
) : RouteRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Volatile
    private var cache: Map<Route.Id, Route>? = null

    override suspend fun list(): List<Route> {
        delay(SIMULATED_NETWORK_DELAY)
        return loadAll().values.sortedBy { it.id.value }
    }

    override suspend fun get(id: Route.Id): Route {
        delay(SIMULATED_NETWORK_DELAY)
        return loadAll().getValue(id)
    }

    private suspend fun loadAll(): Map<Route.Id, Route> = cache ?: withContext(Dispatchers.IO) {
        val index = context.assets.open("routes/index.json").use { stream ->
            json.decodeFromString<List<Long>>(stream.bufferedReader().readText())
        }
        val routes = index.associate { routeId ->
            val dto = context.assets.open("routes/$routeId.json").use { stream ->
                json.decodeFromString<RouteDto>(stream.bufferedReader().readText())
            }
            val route = mapper.map(dto)
            route.id to route
        }
        cache = routes
        routes
    }

    private companion object {
        val SIMULATED_NETWORK_DELAY = 900.milliseconds
    }
}
