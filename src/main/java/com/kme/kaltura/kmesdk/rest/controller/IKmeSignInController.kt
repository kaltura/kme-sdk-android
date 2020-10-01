package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLogoutResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeRegisterResponse

interface IKmeSignInController {

    fun register(
        fullName: String,
        email: String,
        password: String,
        forceRegister: Int,
        addToMailingList: Int,
        success: (response: KmeRegisterResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun login(
        email: String,
        password: String,
        success: (response: KmeLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun logout(
        success: (response: KmeLogoutResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
