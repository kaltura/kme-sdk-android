package com.kme.kaltura.kmesdk.rest

import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import okhttp3.Interceptor
import okhttp3.Response

internal class CsrfTokenInterceptor(
    private val kmePreferences: IKmePreferences
) : Interceptor {

    /**
     * Observes, modifies, and potentially short-circuits requests going out and the corresponding
     * responses coming back in. Typically interceptors add, remove, or transform headers on the request
     * or response.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val csrf = kmePreferences.getString(KmePrefsKeys.CSRF_TOKEN) ?: ""
        val request = chain.request().newBuilder()
                .addHeader("x-csrf-token", csrf)
                .build()
        return chain.proceed(request)
    }

}
