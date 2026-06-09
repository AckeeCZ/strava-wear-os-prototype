package cz.ackee.strava.wearos.core.presentation.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

fun GoogleMap.drawPolyline(
    polyline: List<LatLng>,
    colorArgb: Int,
    widthPx: Float = DEFAULT_POLYLINE_WIDTH_PX,
    cameraPaddingPx: Int = DEFAULT_CAMERA_PADDING_PX,
    onReady: (() -> Unit)? = null,
) {
    if (polyline.isEmpty()) {
        onReady?.invoke()
        return
    }
    addPolyline(
        PolylineOptions()
            .addAll(polyline)
            .color(colorArgb)
            .width(widthPx),
    )
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

private const val DEFAULT_POLYLINE_WIDTH_PX = 8f
private const val DEFAULT_CAMERA_PADDING_PX = 24
