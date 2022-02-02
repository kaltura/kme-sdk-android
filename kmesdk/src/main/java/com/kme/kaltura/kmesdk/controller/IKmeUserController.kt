package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserSetting
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant

/**
 * An interface for actual user information details
 */
interface IKmeUserController {

    /**
     * Getting stored user information in the room
     *
     * @return [KmeParticipant] object as actual user
     */
    fun getCurrentParticipant(): KmeParticipant?

    /**
     * Updates actual user info in the room
     *
     * @param participant data to be stored
     */
    fun updateParticipant(participant: KmeParticipant?)

    /**
     * Checks is actual user is logged and access token exist
     *
     * @return 'true' in case there is a stored data related to actual user
     */
    fun isLoggedIn(): Boolean

    /**
     * Checks is actual user has admin permissions for specific company
     *
     * @param companyId companyId to check
     * @return 'true' in case user has admin permissions
     */
    fun isAdminFor(companyId: Long): Boolean

    /**
     * Checks is actual user has moderator permissions
     *
     * @return 'true' in case user has moderator permissions
     */
    fun isModerator(): Boolean

    /**
     * Getting actual user information
     *
     * @param success function to handle success result. Contains [KmeGetUserInfoResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getUserInformation(
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting actual user information for specific room by alias
     *
     * @param roomAlias alias of a room
     * @param success function to handle success result. Contains [KmeGetUserInfoResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getUserInformation(
        roomAlias: String,
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting stored user information
     *
     * @return [KmeUserInfoData] object as actual user
     */
    fun getCurrentUserInfo(): KmeUserInfoData?

    /**
     * Getting stored user setting
     *
     * @return [KmeUserSetting] object as actual user
     */
    fun getCurrentUserSetting(): KmeUserSetting


    /**
     * Removes actual user information
     */
    fun clearUserInfo()

}
