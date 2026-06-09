package cz.ackee.strava.wearos.core.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import cz.ackee.strava.wearos.core.presentation.navigation.StravaNavDisplay
import cz.ackee.strava.wearos.core.presentation.theme.StravaTheme

@Composable
fun StravaApp() {
    StravaTheme {
        AppScaffold {
            StravaNavDisplay()
        }
    }
}
