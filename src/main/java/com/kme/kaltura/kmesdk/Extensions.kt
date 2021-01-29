package com.kme.kaltura.kmesdk

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Size
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kme.kaltura.kmesdk.ws.message.KmeMessage


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
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                this@glide.setImageDrawable(resource)
                onSizeReady?.invoke(Size(resource.intrinsicWidth, resource.intrinsicHeight))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

internal fun ImageView?.glide(
    decodedString: ByteArray,
    onSizeReady: ((Size) -> Unit)? = null
) {
    if (this == null) return
    Glide.with(this)
        .asDrawable()
        .load(decodedString)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
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
    onSizeReady: ((Size) -> Unit)? = null
) {
    if (this == null) return
    Glide.with(this)
        .asDrawable()
        .load(generateGlideUrl(imageUrl, cookie, fileUrl))
        .apply {
            func?.let { it() }
        }
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                this@glide.setImageDrawable(resource)
                onSizeReady?.invoke(Size(resource.intrinsicWidth, resource.intrinsicHeight))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
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