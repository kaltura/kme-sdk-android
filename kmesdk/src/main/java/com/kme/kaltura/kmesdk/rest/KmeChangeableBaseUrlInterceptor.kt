package com.kme.kaltura.kmesdk.rest

import android.content.Context
import com.kme.kaltura.kmesdk.BuildConfig
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.util.ServerConfiguration
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URL

class KmeChangeableBaseUrlInterceptor(
    context: Context,
    private val prefs: IKmePreferences
) : Interceptor {

    @Volatile
    private var httpUrl: HttpUrl? = null

    private var baseUrl: String

    private var apiUrlPattern = "https://%s/backend/"
    private var devHost = context.getString(R.string.dev_api_url)
    private var stagingHost = context.getString(R.string.staging_api_url)
    private var productionHost = context.getString(R.string.production_api_url)
    private val frpHost = context.getString(R.string.frp_api_url)
    private val capHost = context.getString(R.string.cap_api_url)
    private val cap1Host = context.getString(R.string.cap1_api_url)
    private val vrsqaHost = context.getString(R.string.vrsqa_api_url)

    init {
        val lastUsedUrl = prefs.getString(KmePrefsKeys.BASE_SERVER_URL)
        baseUrl = if (lastUsedUrl.isNullOrEmpty()) {
            if (BuildConfig.DEBUG) {
                String.format(apiUrlPattern, stagingHost)
            } else {
                String.format(apiUrlPattern, productionHost)
            }
        } else {
            lastUsedUrl
        }

        this.httpUrl = baseUrl.toHttpUrlOrNull()
    }

    fun setServerConfiguration(configuration: ServerConfiguration) {
        val url = when (configuration) {
            ServerConfiguration.DEV -> String.format(apiUrlPattern, devHost)
            ServerConfiguration.STAGING -> String.format(apiUrlPattern, stagingHost)
            ServerConfiguration.PRODUCTION -> String.format(apiUrlPattern, productionHost)
            ServerConfiguration.FRP -> String.format(apiUrlPattern, frpHost)
            ServerConfiguration.CAP -> String.format(apiUrlPattern, capHost)
            ServerConfiguration.CAP1 -> String.format(apiUrlPattern, cap1Host)
            ServerConfiguration.VRSQA -> String.format(apiUrlPattern, vrsqaHost)
        }

        if (baseUrl != url) {
            baseUrl = url
            prefs.putString(KmePrefsKeys.BASE_SERVER_URL, baseUrl)
            this.httpUrl = url.toHttpUrlOrNull()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        httpUrl?.let {
            val newUrl = chain.request().url.newBuilder()
                .scheme(it.scheme)
                .host(it.host)
                .port(it.port)
                .build()

            val newRequest = chain.request().newBuilder()
                .url(newUrl)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(chain.request())
    }

    fun isSameServerConfiguration(link: String?): Boolean? {
        if (link.isNullOrEmpty()) return null

        val url = URL(link)

        val newConfiguration = when (url.host) {
            devHost -> ServerConfiguration.DEV
            stagingHost -> ServerConfiguration.STAGING
            productionHost -> ServerConfiguration.PRODUCTION
            frpHost -> ServerConfiguration.FRP
            capHost -> ServerConfiguration.CAP
            cap1Host -> ServerConfiguration.CAP1
            vrsqaHost -> ServerConfiguration.VRSQA
            else -> return null
        }

        return getServerConfiguration() == newConfiguration
    }

    fun getServerConfiguration(): ServerConfiguration? {
        return when (httpUrl?.host) {
            devHost -> ServerConfiguration.DEV
            stagingHost -> ServerConfiguration.STAGING
            productionHost -> ServerConfiguration.PRODUCTION
            frpHost -> ServerConfiguration.FRP
            capHost -> ServerConfiguration.CAP
            cap1Host -> ServerConfiguration.CAP1
            vrsqaHost -> ServerConfiguration.VRSQA
            else -> null
        }
    }
}