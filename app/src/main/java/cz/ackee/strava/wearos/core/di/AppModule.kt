package cz.ackee.strava.wearos.core.di

import cz.ackee.strava.wearos.feature.routes.routesModule
import org.koin.dsl.module

val appModule = module {
    includes(routesModule)
}
