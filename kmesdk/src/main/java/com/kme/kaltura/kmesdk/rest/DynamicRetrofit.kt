package com.kme.kaltura.kmesdk.rest

import android.content.Context
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.util.ServerConfiguration
import okhttp3.OkHttpClient
import org.koin.core.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DynamicRetrofit(
    context: Context,
    private val okHttpClient: OkHttpClient
) : KmeKoinComponent {

    private val gson: Gson by inject()

    private var baseUrl = "https://%s/backend/"
    private var stagingUrl = String.format(baseUrl, context.getString(R.string.staging_api_url))
    private var productionUrl =
        String.format(baseUrl, context.getString(R.string.production_api_url))

    init {
        baseUrl = if (BuildConfig.DEBUG) {
            stagingUrl
        } else {
            productionUrl
        }
    }

    private fun buildApi() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private var api: Retrofit = buildApi()

    fun <T> create(service: Class<T>): T {
        return api.create(service)
    }

    fun setServerConfiguration(configuration: ServerConfiguration) {
        val url = when (configuration) {
            ServerConfiguration.STAGING -> stagingUrl
            ServerConfiguration.PRODUCTION -> productionUrl
        }

        if (baseUrl != url) {
            baseUrl = url
            api = buildApi()
        }
    }

    fun getServerConfiguration() : ServerConfiguration? {
       return when (baseUrl) {
            stagingUrl -> ServerConfiguration.STAGING
            productionUrl -> ServerConfiguration.PRODUCTION
            else -> null
        }
    }
}