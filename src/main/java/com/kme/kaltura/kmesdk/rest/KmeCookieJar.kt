package com.kme.kaltura.kmesdk.rest

import com.kaltura.playkit.utils.NativeCookieJarBridge
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class KmeCookieJar(
    private val prefs: IKmePreferences
) : CookieJar {

    private val cookies = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookieList: List<Cookie>) {
        if (url.toString().contains("fe/metadata")) {
            prefs.putString(KmePrefsKeys.COOKIE, cookieList.joinToString(";"))
        }
        cookies.addAll(cookieList)

//        NativeCookieJarBridge.sharedCookieJar.saveFromResponse(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        cookies
}