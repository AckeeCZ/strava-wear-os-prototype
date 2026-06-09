package cz.ackee.strava.wearos.feature.routes.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme

@Composable
fun RouteDetailScreen(routeId: Long) {
    ScreenScaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Route detail $routeId")
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RouteDetailScreenPreview() {
    StravaTheme {
        RouteDetailScreen(routeId = 1L)
    }
}
