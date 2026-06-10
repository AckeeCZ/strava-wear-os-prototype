package cz.ackee.strava.wearos.feature.routes.domain.model

import com.google.android.gms.maps.model.LatLng
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

data class Segment(
    val id: Id,
    val name: String,
    val distanceMeters: Double,
    val gradeAverage: Double,
    val gradeMaximum: Double,
    val elevationHighMeters: Double,
    val elevationLowMeters: Double,
    val climbCategory: Int,
    val location: String?,
    val startLatLng: LatLng,
    val endLatLng: LatLng,
    val polyline: List<LatLng>,
) {

    val elevationGainMeters: Int
        get() = (elevationHighMeters - elevationLowMeters)
            .coerceAtLeast(0.0)
            .roundToInt()

    @JvmInline
    value class Id(val value: Long)
}
