package com.kme.kaltura.kmesdk

import android.app.Application
import com.kme.kaltura.kmesdk.di.restModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.dsl.koinApplication

class KmeSdkApplication : Application() {

    companion object {
        lateinit var koin: Koin
    }

    override fun onCreate() {
        super.onCreate()
        koin = koinApplication {
            androidContext(applicationContext)
            modules(restModule)
        }.koin
    }

}