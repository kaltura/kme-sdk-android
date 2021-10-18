package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeUserApiService
import com.kme.kaltura.kmesdk.util.extensions.isModerator
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for actual user information details
 */
class KmeUserControllerImpl : KmeController(), IKmeUserController {

    private val userApiService: KmeUserApiService by inject()
    private val kmePreferences: IKmePreferences by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var currentUserInfo: KmeUserInfoData? = null
    private var currentParticipant: KmeParticipant? = null

    /**
     * Checks is actual user is logged and access token exist
     */
    override fun isLoggedIn(): Boolean {
        return !kmePreferences.getString(KmePrefsKeys.ACCESS_TOKEN, "").isNullOrEmpty()
    }

    /**
     * Checks is actual user has admin permissions for specific company
     */
    override fun isAdminFor(companyId: Long): Boolean {
        getCurrentUserInfo()?.userCompanies?.companies?.find {
            it.id == companyId
        }?.let {
            return (it.role == KmeUserRole.INSTRUCTOR ||
                    it.role == KmeUserRole.ADMIN ||
                    it.role == KmeUserRole.OWNER)
        }
        return false
    }

    /**
     * Checks is actual user has moderator permissions
     */
    override fun isModerator(): Boolean {
        return getCurrentParticipant()?.isModerator() == true
    }

    /**
     * Getting actual user information
     */
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

    /**
     * Getting actual user information for specific room by alias
     */
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

    /**
     * Getting stored user information
     */
    override fun getCurrentUserInfo() = currentUserInfo

    /**
     * Getting stored user information in the room
     */
    override fun getCurrentParticipant() = currentParticipant

    /**
     * Updates actual user info in the room
     */
    override fun updateParticipant(participant: KmeParticipant?) {
        this.currentParticipant = participant
    }

    override fun clearUserInfo() {
        currentUserInfo = null
        currentParticipant = null
        kmePreferences.clear()
    }

}
