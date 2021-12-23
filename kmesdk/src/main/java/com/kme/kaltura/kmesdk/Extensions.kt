package com.kme.kaltura.kmesdk

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Size
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

internal fun removeCookies(callback: () -> Unit) {
    val cookieManager = CookieManager.getInstance()
    if (cookieManager.hasCookies()) {
        cookieManager.removeAllCookies {
            callback()
        }
    } else {
        callback()
    }
}

fun String.encryptWith(key: String): String {
    val s = IntArray(256)
    var x: Int

    for (i in 0..255) {
        s[i] = i
    }

    var j = 0
    for (i in 0..255) {
        j = (j + s[i] + key[i % key.length].toInt()) % 256
        x = s[i]
        s[i] = s[j]
        s[j] = x
    }

    var i = 0
    j = 0

    var result = ""
    for (element in this) {
        i = (i + 1) % 256
        j = (j + s[i]) % 256
        x = s[i]
        s[i] = s[j]
        s[j] = x
        result += (element.toInt() xor s[(s[i] + s[j]) % 256]).toChar()
    }
    return result
}

fun String?.toNonNull(default: String = "") = this ?: default

inline fun <A, B, R> ifNonNull(a: A?, b: B?, block: (a: A, b: B) -> R): R? {
    return if (a != null && b != null) {
        block(a, b)
    } else null
}

inline fun <A, B, C, R> ifNonNull(a: A?, b: B?, c: C?, block: (a: A, b: B, c: C) -> R): R? {
    return if (a != null && b != null && c != null) {
        block(a, b, c)
    } else null
}

inline fun <reified T> KmeMessage<*>.toType(): T? =
    if (this is T)
        @Suppress("UNCHECKED_CAST")
        this else
        null

internal fun Context?.getBitmap(
    imageUrl: String?,
    cookie: String?,
    fileUrl: String?
): Bitmap? {
    if (this == null || imageUrl.isNullOrEmpty()) return null
    return Glide.with(this)
        .asBitmap()
        .load(generateGlideUrl(imageUrl, cookie, fileUrl))
        .submit().get()
}

internal fun ImageView?.glide(
    drawable: Int,
    onSizeReady: ((Size) -> Unit)? = null
) {
    if (this == null) return
    Glide.with(this)
        .load(drawable)
        .skipMemoryCache(true)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                this@glide.setImageDrawable(resource)
                onSizeReady?.invoke(Size(resource.intrinsicWidth, resource.intrinsicHeight))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

internal fun ImageView?.glide(
    imageUrl: String?,
    cookie: String?,
    fileUrl: String?,
    func: (RequestBuilder<Drawable>.() -> RequestBuilder<Drawable>)? = null,
) {
    if (this == null) return
    Glide.with(this)
        .load(generateGlideUrl(imageUrl, cookie, fileUrl))
        .apply {
            func?.let { it() }
        }
        .into(this)
}

internal fun ImageView?.glide(
    imageUrl: String?,
    cookie: String?,
    fileUrl: String?,
    func: (RequestBuilder<Drawable>.() -> RequestBuilder<Drawable>)? = null,
    onSizeReady: ((Size) -> Unit)? = null
) {
    if (this == null) return
    Glide.with(this)
        .asDrawable()
        .load(generateGlideUrl(imageUrl, cookie, fileUrl))
        .skipMemoryCache(true)
        .apply {
            func?.let { it() }
        }
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?,
            ) {
                when(resource){
                    is GifDrawable ->{
                        this@glide.setImageDrawable(resource.apply {
                            start()
                        })
                    }
                    else -> {
                        val scaledBitmap = (resource as BitmapDrawable).bitmap.resize(1280)
                        this@glide.setImageBitmap(scaledBitmap)
                    }
                }
                onSizeReady?.invoke(Size(resource.intrinsicWidth, resource.intrinsicHeight))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

internal fun Bitmap.resize(maxSize: Int): Bitmap? {
    var width = width
    var height = height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(this, width, height, true)
}

internal fun generateGlideUrl(url: String?, cookie: String?, fileUrl: String?): GlideUrl? {
    return if (url.isNullOrEmpty()) {
        null
    } else {
        var filesUrl = url

        if (!URLUtil.isValidUrl(url)) {
            fileUrl?.let {
                filesUrl = it.plus(url)
            }
        }

        GlideUrl(
            filesUrl,
            LazyHeaders.Builder()
                .addHeader("Cookie", cookie ?: "")
                .build()
        )
    }
}

fun Context.isLandscape(): Boolean {
    return resources.isLandscape()
}

fun Resources.isLandscape(): Boolean {
    val orientation = configuration.orientation
    return orientation == Configuration.ORIENTATION_LANDSCAPE
}

