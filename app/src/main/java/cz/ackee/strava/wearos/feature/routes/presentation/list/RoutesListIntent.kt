package cz.ackee.strava.wearos.feature.routes.presentation.list

sealed interface RoutesListIntent {

    data object Retry : RoutesListIntent
}
