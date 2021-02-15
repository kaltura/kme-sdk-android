package com.kme.kaltura.kmesdk.rest

import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Provides **policy** and **persistence** for HTTP cookies.
 *
 * @property prefs KME preferences
 */
class KmeCookieJar(
    private val prefs: IKmePreferences
) : CookieJar {

    private val cookies = mutableListOf<Cookie>()

    /**
     * Save cookies only from metadata response
     */
    override fun saveFromResponse(url: HttpUrl, cookieList: List<Cookie>) {
        if (url.toString().contains("fe/metadata")) {
            prefs.putString(KmePrefsKeys.COOKIE, cookieList.joinToString(";"))
        }
        cookies.addAll(cookieList)
    }

    /**
     * Load cookies from the jar for an HTTP request to [url]. This method returns a possibly
     * empty list of cookies for the network request.
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        cookies
}