package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView

class ConnectionPreviewViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    fun createPreview(preview: KmeSurfaceRendererView) {
        kmeSdk.roomController.peerConnectionModule.startPreview(preview)
    }

    fun enablePreview(isEnable: Boolean) {
        kmeSdk.roomController.peerConnectionModule.enableCamera(isEnable)
    }

    fun switchCamera() {
        kmeSdk.roomController.peerConnectionModule.switchCamera()
    }

    fun stopPreview() {
        kmeSdk.roomController.peerConnectionModule.stopPreview()
    }

}
