package cz.ackee.strava.wearos.feature.routes

import cz.ackee.strava.wearos.feature.routes.data.MockRouteRepository
import cz.ackee.strava.wearos.feature.routes.domain.Route
import cz.ackee.strava.wearos.feature.routes.domain.RouteRepository
import cz.ackee.strava.wearos.feature.routes.presentation.detail.RouteDetailViewModel
import cz.ackee.strava.wearos.feature.routes.presentation.list.RoutesListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val routesModule = module {
    single<RouteRepository> { MockRouteRepository(androidContext()) }
    viewModel { RoutesListViewModel(repository = get()) }
    viewModel { (id: Route.Id) -> RouteDetailViewModel(id = id, repository = get()) }
}
