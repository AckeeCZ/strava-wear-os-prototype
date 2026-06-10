package cz.ackee.strava.wearos.core.presentation.map

import android.os.Bundle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch

@Composable
fun WearMapStatic(
    mapStyle: MapStyleOptions,
    placeholderColor: Color,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    configureMap: GoogleMap.(onReady: () -> Unit) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var ready by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (ready) 1f else 0f,
        animationSpec = tween(durationMillis = FADE_IN_MS),
        label = "wearMapStaticAlpha",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(placeholderColor),
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha),
            factory = { ctx ->
                MapView(ctx, GoogleMapOptions().liteMode(true)).apply {
                    onCreate(Bundle())
                    onStart()
                    onResume()
                }
            },
            update = { mapView ->
                coroutineScope.launch {
                    mapView.awaitMap().apply {
                        applyWearDefaults(mapStyle)
                        configureMap { ready = true }
                    }
                }
            },
            onRelease = { mapView ->
                mapView.onPause()
                mapView.onStop()
                mapView.onDestroy()
            },
        )
    }
}

private const val FADE_IN_MS = 200
