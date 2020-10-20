package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.encryptWith
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLogoutResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeRegisterResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeSignInApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeSignInControllerImpl : KmeController(), IKmeSignInController {

    private val signInApiService: KmeSignInApiService by inject()
    private val prefs: IKmePreferences by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun register(
        fullName: String,
        email: String,
        password: String,
        forceRegister: Int,
        addToMailingList: Int,
        success: (response: KmeRegisterResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        val encryptedPassword = password.encryptWith(PASS_ENCRYPT_KEY)
        uiScope.launch {
            safeApiCall(
                {
                    signInApiService.register(
                        fullName,
                        email,
                        encryptedPassword,
                        forceRegister,
                        addToMailingList
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
        success: (response: KmeLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        val encryptedPassword = password.encryptWith(PASS_ENCRYPT_KEY)
        uiScope.launch {
            safeApiCall(
                { signInApiService.login(email, encryptedPassword) },
                { response ->
                    response.data?.accessToken?.let { token ->
                        prefs.putString(KmePrefsKeys.ACCESS_TOKEN, token)
                    }
                    success(response)
                },
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

    companion object {
        const val PASS_ENCRYPT_KEY = "8kjbca328hbvcm,z,123A"
    }

}