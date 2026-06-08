package cz.ackee.strava.wearos.feature.routes.presentation.map

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RouteMapDestination(val routeId: Long) : NavKey
