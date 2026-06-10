package cz.ackee.strava.wearos.feature.routes.domain.repository

import cz.ackee.strava.wearos.feature.routes.domain.model.Route

interface RouteRepository {

    suspend fun list(): List<Route>

    suspend fun get(id: Route.Id): Route
}
