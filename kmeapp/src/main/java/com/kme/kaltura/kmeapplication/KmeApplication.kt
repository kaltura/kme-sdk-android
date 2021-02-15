package com.kme.kaltura.kmeapplication

import android.app.Application
import com.kme.kaltura.kmeapplication.di.preferencesModule
import com.kme.kaltura.kmeapplication.di.sdk
import com.kme.kaltura.kmeapplication.di.utilModule
import com.kme.kaltura.kmeapplication.di.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KmeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(sdk)
            modules(viewModel)
            modules(preferencesModule)
            modules(utilModule)
        }
    }

}
