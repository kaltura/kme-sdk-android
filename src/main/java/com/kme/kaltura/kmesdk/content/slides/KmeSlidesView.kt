package com.kme.kaltura.kmesdk.content.slides

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class KmeSlidesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    class Config(
        val cookie: String,
    )

    companion object

}