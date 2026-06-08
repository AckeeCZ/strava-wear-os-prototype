package cz.ackee.strava.wearos

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import cz.ackee.strava.wearos.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class StravaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@StravaApplication)
            modules(appModule)
        }
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) { }
    }
}
