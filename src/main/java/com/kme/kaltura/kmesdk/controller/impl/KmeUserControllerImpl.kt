package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeUserApiService
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeUserControllerImpl : KmeController(), IKmeUserController {

    private val userApiService: KmeUserApiService by inject()
    private val kmePreferences: IKmePreferences by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var currentUserInfo: KmeUserInfoData? = null

    override var currentParticipant: KmeParticipant? = null

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
                success = {
                    currentUserInfo = it.data
                    success(it)
                },
                error = {
                    currentUserInfo = null
                    error(it)
                }
            )
        }
    }

    override fun getUserInformation(
        roomAlias: String,
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { userApiService.getUserInfo(roomAlias) },
                success = {
                    currentUserInfo = it.data
                    success(it)
                },
                error = {
                    currentUserInfo = null
                    error(it)
                }
            )
        }
    }

    override fun getCurrentUserInfo() = currentUserInfo

}
