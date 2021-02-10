package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME

class RoomRenderersViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    fun getPublisherId() = kmeSdk.userController.getCurrentUserInfo()?.id

}
