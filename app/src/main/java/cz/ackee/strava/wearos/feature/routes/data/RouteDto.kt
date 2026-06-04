package cz.ackee.strava.wearos.feature.routes.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteDto(
    val id: Long,
    val name: String,
    val distance: Double,
    @SerialName("elevation_gain") val elevationGain: Double,
    @SerialName("estimated_moving_time") val estimatedMovingTime: Long,
    val map: PolylineMapDto,
)

@Serializable
data class PolylineMapDto(
    val polyline: String? = null,
    @SerialName("summary_polyline") val summaryPolyline: String,
)
