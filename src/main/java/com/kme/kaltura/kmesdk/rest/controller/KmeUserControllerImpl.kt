package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeUserApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeUserControllerImpl : KmeKoinComponent, IKmeUserController {

    private val userApiService: KmeUserApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun getUserInformation(
        accessToken: String,
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { userApiService.getUserInfo(accessToken) },
                success,
                error
            )
        }
    }

}
