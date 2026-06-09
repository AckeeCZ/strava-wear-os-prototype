package cz.ackee.strava.wearos.feature.routes.presentation.detail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RouteDetailDestination(val routeId: Long) : NavKey
