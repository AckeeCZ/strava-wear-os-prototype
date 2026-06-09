package cz.ackee.strava.wearos.feature.routes.domain

interface RouteRepository {

    suspend fun list(): List<Route>

    suspend fun get(id: Route.Id): Route
}
