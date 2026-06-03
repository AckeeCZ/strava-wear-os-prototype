package cz.ackee.strava.wearos.feature.routes.presentation.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme

@Composable
fun RoutesListScreen(onRouteClick: (routeId: Long) -> Unit) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(state = listState, contentPadding = contentPadding) {
            item {
                ListHeader { Text("Routes") }
            }
            item {
                Button(onClick = { onRouteClick(1L) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Route 1 (placeholder)")
                }
            }
            item {
                Button(onClick = { onRouteClick(2L) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Route 2 (placeholder)")
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun RoutesListScreenPreview() {
    StravaTheme {
        RoutesListScreen(onRouteClick = {})
    }
}
