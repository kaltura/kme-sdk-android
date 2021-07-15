package com.kme.kaltura.kmesdk.content

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.kme.kaltura.kmesdk.ui.widget.overlap.IFloatingContainer

abstract class KmeOverlapFragment: Fragment() {

    var isMediaOverlay: Boolean = false

    var floatingCallback: IFloatingContainer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setZOrderMediaOverlay(isMediaOverlay)
    }

    private fun View.setZOrderMediaOverlay(isMediaOverlay: Boolean) {
        if (this is SurfaceView) {
            this.setZOrderMediaOverlay(isMediaOverlay)
        } else if (this is ViewGroup) {
            this.forEach {
                it.setZOrderMediaOverlay(isMediaOverlay)
            }
        }
    }

}