package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.koin
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLogoutResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeRegisterResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeSignInApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

class KmeSignInControllerImpl : KoinComponent, IKmeSignInController {

    private val signInApiService: KmeSignInApiService by koin.inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun register(
        fullName: String,
        email: String,
        password: String,
        forceRegister: Int,
        addToMailingList: Int,
        captchaToken: String,
        success: (response: KmeRegisterResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                {
                    signInApiService.register(
                        fullName,
                        email,
                        password,
                        forceRegister,
                        addToMailingList,
                        captchaToken
                    )
                },
                success,
                error
            )
        }
    }

    override fun login(
        email: String,
        password: String,
        captchaToken: String,
        success: (response: KmeLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { signInApiService.login(email, password, captchaToken) },
                success,
                error
            )
        }
    }

    override fun logout(
        success: (response: KmeLogoutResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { signInApiService.logout() },
                success,
                error
            )
        }
    }

}
