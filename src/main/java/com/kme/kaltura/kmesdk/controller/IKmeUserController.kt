package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData

interface IKmeUserController {

    fun isLoggedIn(): Boolean

    fun getUserInformation(
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getCurrentUserInfo() : KmeUserInfoData?

}
