package cz.ackee.strava.wearos.feature.routes.data.dto

import com.google.android.gms.maps.model.LatLng
import cz.ackee.strava.wearos.feature.routes.data.serializer.LatLngSerializer
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
    val segments: List<RouteSegmentDto> = emptyList(),
)

@Serializable
data class PolylineMapDto(
    val polyline: String? = null,
    @SerialName("summary_polyline") val summaryPolyline: String,
)

@Serializable
data class RouteSegmentDto(
    val id: Long,
    val name: String,
    val distance: Double,
    @SerialName("average_grade") val averageGrade: Double,
    @SerialName("maximum_grade") val maximumGrade: Double,
    @SerialName("elevation_high") val elevationHigh: Double,
    @SerialName("elevation_low") val elevationLow: Double,
    @SerialName("climb_category") val climbCategory: Int,
    @SerialName("start_latlng") @Serializable(with = LatLngSerializer::class) val startLatLng: LatLng,
    @SerialName("end_latlng") @Serializable(with = LatLngSerializer::class) val endLatLng: LatLng,
    val city: String? = null,
    val country: String? = null,
)
