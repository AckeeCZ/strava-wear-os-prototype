package cz.ackee.strava.wearos.core.presentation.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.widget.SwipeDismissFrameLayout
import androidx.wear.widget.SwipeDismissFrameLayout.Callback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch

@Composable
fun WearMap(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    configureMap: GoogleMap.() -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()
    val currentOnBack by rememberUpdatedState(onBack)

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            val mapView = MapView(ctx, GoogleMapOptions().ambientEnabled(true))
            bindLifecycle(mapView, lifecycleOwner)
            bindAmbient(mapView, lifecycleOwner, activity)
            SwipeDismissMapLayout(ctx, mapView) { currentOnBack() }
        },
        update = { swipeDismissMapLayout ->
            coroutineScope.launch {
                swipeDismissMapLayout.mapView.awaitMap()
                    .apply {
                        applyWearDefaults()
                        configureMap()
                    }
            }
        },
        onRelease = { swipeDismissMapLayout ->
            swipeDismissMapLayout.mapView.onDestroy()
        },
    )
}

@SuppressLint("ViewConstructor")
private class SwipeDismissMapLayout(
    context: Context,
    mapView: MapView,
    onBack: () -> Unit,
) : SwipeDismissFrameLayout(context) {

    init {
        addView(mapView)
        addCallback(object : Callback() {
            override fun onSwipeStarted(layout: SwipeDismissFrameLayout) = onBack()
        })
    }

    val mapView: MapView get() = getChildAt(0) as MapView
}

private fun bindAmbient(
    mapView: MapView,
    lifecycleOwner: LifecycleOwner,
    activity: Activity?,
) {
    if (activity == null) return
    val observer = AmbientLifecycleObserver(
        activity,
        object : AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(
                ambientDetails: AmbientLifecycleObserver.AmbientDetails,
            ) {
                mapView.onEnterAmbient(Bundle())
            }

            override fun onExitAmbient() {
                mapView.onExitAmbient()
            }

            override fun onUpdateAmbient() = Unit
        },
    )
    mapView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) =
            lifecycleOwner.lifecycle.addObserver(observer)

        override fun onViewDetachedFromWindow(v: View) =
            lifecycleOwner.lifecycle.removeObserver(observer)
    })
}

private fun bindLifecycle(mapView: MapView, lifecycleOwner: LifecycleOwner) {
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

private fun GoogleMap.applyWearDefaults() {
    uiSettings.apply {
        isZoomControlsEnabled = false
        isCompassEnabled = false
        isMapToolbarEnabled = false
        isMyLocationButtonEnabled = false
        isIndoorLevelPickerEnabled = false
    }
}
