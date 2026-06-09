package cz.ackee.strava.wearos.feature.routes.domain

import com.google.android.gms.maps.model.LatLng
import kotlin.jvm.JvmInline
import kotlin.time.Duration

data class Route(
    val id: Id,
    val name: String,
    val distanceMeters: Double,
    val elevationGainMeters: Double,
    val estimatedMovingTime: Duration,
    val polyline: List<LatLng>,
) {

    @JvmInline
    value class Id(val value: Long)
}
