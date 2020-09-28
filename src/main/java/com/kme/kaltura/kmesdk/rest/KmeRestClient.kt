package com.kme.kaltura.kmesdk.rest

import com.kme.kaltura.kmesdk.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class KmeRestClient {

    companion object {
        private var okHttpClient: OkHttpClient? = null

        fun getInstance(): KmeRestClient {
            if (okHttpClient == null) {
                okHttpClient = getOkHttpClient()
            }
            return KmeRestClient()
        }

        private fun getOkHttpClient(): OkHttpClient? {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .build()
        }
    }

}
