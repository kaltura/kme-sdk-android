package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeChangeRoomSettingsResponse
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

/**
 * An interface for actions related to chat
 */
interface IKmeChatController {

    /**
     * Change visibility of public chat
     *
     * @param roomId id of a room
     * @param value value flag
     * @param success function to handle success result. Contains [KmeChangeRoomSettingsResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun changePublicChatVisibility(
        roomId: Long,
        value: KmePermissionValue,
        success: (response: KmeChangeRoomSettingsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
