package cz.ackee.strava.wearos.feature.routes

import cz.ackee.strava.wearos.feature.routes.data.mapper.RouteMapper
import cz.ackee.strava.wearos.feature.routes.data.repository.MockRouteRepository
import cz.ackee.strava.wearos.feature.routes.data.repository.MockStarredSegmentRepository
import cz.ackee.strava.wearos.feature.routes.domain.model.Route
import cz.ackee.strava.wearos.feature.routes.domain.repository.RouteRepository
import cz.ackee.strava.wearos.feature.routes.domain.repository.StarredSegmentRepository
import cz.ackee.strava.wearos.feature.routes.presentation.detail.RouteDetailViewModel
import cz.ackee.strava.wearos.feature.routes.presentation.list.RoutesListViewModel
import cz.ackee.strava.wearos.feature.routes.presentation.map.RouteMapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val routesModule = module {
    single { RouteMapper() }
    single<RouteRepository> { MockRouteRepository(androidContext(), get()) }
    single<StarredSegmentRepository> { MockStarredSegmentRepository(androidContext(), get()) }
    viewModel { RoutesListViewModel(repository = get()) }
    viewModel { (id: Route.Id) -> RouteDetailViewModel(id = id, repository = get()) }
    viewModel { (id: Route.Id) -> RouteMapViewModel(id = id, repository = get()) }
}
