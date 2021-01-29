package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeChatModule
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeChangeRoomSettingsResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeChatApiService
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation actions related to chat
 */
class KmeChatModuleImpl : KmeController(), IKmeChatModule {

    private val chatApiService: KmeChatApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Change visibility of public chat
     */
    override fun changePublicChatVisibility(
        roomId: Long,
        value: KmePermissionValue,
        success: (response: KmeChangeRoomSettingsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                {
                    chatApiService.changePublicChatVisibility(
                        roomId,
                        KmePermissionModule.CHAT_MODULE.name.toLowerCase(),
                        KmePermissionKey.PUBLIC_CHAT.name.toLowerCase(),
                        value.name.toLowerCase()
                    )
                },
                success,
                error
            )
        }
    }

}
