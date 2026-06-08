package cz.ackee.strava.wearos.feature.routes.presentation.segments

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RouteSegmentsDestination(val routeId: Long) : NavKey
