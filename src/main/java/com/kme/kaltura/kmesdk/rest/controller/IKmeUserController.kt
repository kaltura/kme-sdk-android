package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse

interface IKmeUserController {

    fun isLoggedIn(): Boolean

    fun getUserInformation(
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
