package cz.ackee.strava.wearos.core.presentation.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

fun GoogleMap.drawPolyline(polyline: List<LatLng>, colorArgb: Int) {
    if (polyline.isEmpty()) return
    addPolyline(
        PolylineOptions()
            .addAll(polyline)
            .color(colorArgb)
            .width(POLYLINE_WIDTH_PX),
    )
    val bounds = LatLngBounds.builder().apply {
        polyline.forEach { include(it) }
    }.build()
    moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_PADDING_PX))
}

private const val POLYLINE_WIDTH_PX = 8f
private const val CAMERA_PADDING_PX = 24
