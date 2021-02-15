package com.kme.kaltura.kmeapplication.util.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmesdk.KME
import org.koin.core.context.KoinContextHandler

fun View?.visible() {
    if (this == null) return
    if (!isVisible()) {
        this.visibility = View.VISIBLE
    }
}

fun View?.isVisible(): Boolean {
    if (this == null) return false
    return visibility == View.VISIBLE
}

fun View?.gone() {
    if (this == null) return
    if (!isGone()) {
        this.visibility = View.GONE
    }
}

fun View?.isGone(): Boolean {
    if (this == null) return true
    return visibility == View.GONE
}

fun View?.invisible() {
    if (this == null) return
    if (!isInvisible()) {
        this.visibility = View.INVISIBLE
    }
}

fun View?.isInvisible(): Boolean {
    if (this == null) return false
    return visibility == View.INVISIBLE
}

fun TextView?.goneIfTextEmpty() {
    if (this == null) return
    if (text.isNullOrEmpty()) {
        gone()
    } else {
        visible()
    }
}

fun RadioGroup?.goneIfEmpty() {
    if (this == null) return
    if (childCount == 0) {
        gone()
    } else {
        visible()
    }
}

fun ImageView?.glide(
    imageResource: Int,
    default: Int = R.drawable.ic_default_background,
    func: (RequestBuilder<Drawable>.() -> RequestBuilder<Drawable>)? = null
) {
    if (this == null) return
    Glide.with(this).load(imageResource)
        .apply {
            func?.let { it() }
        }
        .error(default)
        .into(this)
}

fun ImageView?.glide(
    imageUrl: String?,
    default: Int = R.drawable.ic_default_background,
    func: (RequestBuilder<Drawable>.() -> RequestBuilder<Drawable>)? = null
) {
    if (this == null) return
    Glide.with(this).load(generateGlideUrl(imageUrl))
        .apply {
            func?.let { it() }
        }
        .error(default)
        .into(this)
}

private fun generateGlideUrl(url: String?): GlideUrl? {
    return if (url.isNullOrEmpty()) {
        null
    } else {
        val kmeSdk = KoinContextHandler.get().get<KME>()
        var filesUrl = url

        if (!URLUtil.isValidUrl(url)) {
            kmeSdk.getFilesUrl()?.let {
                filesUrl = it.plus(url)
            }
        }

        GlideUrl(
            filesUrl,
            LazyHeaders.Builder()
                .addHeader("Cookie", kmeSdk.getCookies() ?: "")
                .build()
        )
    }
}