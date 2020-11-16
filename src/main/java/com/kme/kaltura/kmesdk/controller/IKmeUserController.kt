package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant

interface IKmeUserController {

    var currentParticipant: KmeParticipant?

    fun isLoggedIn(): Boolean

    fun getUserInformation(
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getUserInformation(
        roomAlias: String,
        success: (response: KmeGetUserInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getCurrentUserInfo(): KmeUserInfoData?

}
