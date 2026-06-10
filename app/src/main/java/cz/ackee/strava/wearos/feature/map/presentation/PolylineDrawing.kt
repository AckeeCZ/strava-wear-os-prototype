package cz.ackee.strava.wearos.feature.map.presentation

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

fun GoogleMap.drawPolyline(
    polyline: List<LatLng>,
    colorArgb: Int,
    widthPx: Float,
) {
    if (polyline.isEmpty()) return
    addPolyline(
        PolylineOptions()
            .addAll(polyline)
            .color(colorArgb)
            .width(widthPx),
    )
}

fun GoogleMap.fitCameraToPolyline(
    polyline: List<LatLng>,
    cameraPaddingPx: Int,
    onReady: (() -> Unit)? = null,
) {
    if (polyline.isEmpty()) {
        onReady?.invoke()
        return
    }
    val bounds = LatLngBounds.builder().apply {
        polyline.forEach { include(it) }
    }.build()
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, cameraPaddingPx)
    if (onReady == null) {
        moveCamera(cameraUpdate)
    } else {
        setOnMapLoadedCallback {
            moveCamera(cameraUpdate)
            onReady()
        }
    }
}
