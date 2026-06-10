package cz.ackee.strava.wearos.feature.map.presentation

import android.os.Bundle
import android.view.View
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions

internal fun bindLifecycle(mapView: MapView, lifecycleOwner: LifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY,
            Lifecycle.Event.ON_ANY,
            -> Unit
        }
    }
    mapView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) =
            lifecycleOwner.lifecycle.addObserver(observer)

        override fun onViewDetachedFromWindow(v: View) =
            lifecycleOwner.lifecycle.removeObserver(observer)
    })
}

internal fun GoogleMap.applyWearDefaults(style: MapStyleOptions) {
    uiSettings.apply {
        isZoomControlsEnabled = false
        isCompassEnabled = false
        isMapToolbarEnabled = false
        isMyLocationButtonEnabled = false
        isIndoorLevelPickerEnabled = false
    }
    setMapStyle(style)
}

@Composable
internal fun rememberMapStyle(@RawRes styleRes: Int): MapStyleOptions {
    val context = LocalContext.current
    return remember(context, styleRes) {
        MapStyleOptions.loadRawResourceStyle(context, styleRes)
    }
}
