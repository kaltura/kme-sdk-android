package com.kme.kaltura.kmeapplication.di

import com.google.gson.Gson
import com.kme.kaltura.kmeapplication.util.TimeUtil
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val utilModule = module {
    single { Gson() }
    single { TimeUtil() }
}