package com.kme.kaltura.kmesdk.content

import android.os.Bundle
import android.view.View
import com.kme.kaltura.kmesdk.di.KmeKoinComponent

/**
 * Base abstract class for representation shared content
 */
abstract class KmeContentView: KmeOverlapFragment(), KmeKoinComponent {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
        view.isFocusable = true
    }

}