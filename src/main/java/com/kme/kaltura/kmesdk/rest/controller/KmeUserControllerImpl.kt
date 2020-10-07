package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeUserApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeUserControllerImpl : KmeController(), IKmeUserController  {

    private val userApiService: KmeUserApiService by inject()
    private val kmePreferences: IKmePreferences by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun isLoggedIn(): Boolean {
        return !kmePreferences.getString(KmePrefsKeys.ACCESS_TOKEN, "").isNullOrEmpty()
    }

    override fun getUserInformation(
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { userApiService.getUserInfo() },
                success,
                error
            )
        }
    }

}
