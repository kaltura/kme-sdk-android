package com.kme.kaltura.kmesdk.rest

import android.webkit.CookieManager
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

    /**
     * Save cookies only from metadata response
     */
    override fun saveFromResponse(url: HttpUrl, cookieList: List<Cookie>) {
        if (url.toString().contains("fe/metadata")) {
            prefs.putString(KmePrefsKeys.COOKIE, cookieList.joinToString(";"))
        }

        val cookieManager = CookieManager.getInstance()
        for (cookie in cookieList) {
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
    }

    /**
     * Load cookies from the jar for an HTTP request to [url]. This method returns a possibly
     * empty list of cookies for the network request.
     */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieManager = CookieManager.getInstance()
        val cookieList: MutableList<Cookie> = ArrayList()
        val cookies = cookieManager.getCookie(url.toString())
        if (!cookies.isNullOrEmpty()) {
            val splitCookies = cookies.split("[,;]".toRegex()).toTypedArray()
            for (i in splitCookies.indices) {
                Cookie.parse(url, splitCookies[i].trim { it <= ' ' })?.let {
                    cookieList.add(it)
                }
            }
        }
        return cookieList
    }
}