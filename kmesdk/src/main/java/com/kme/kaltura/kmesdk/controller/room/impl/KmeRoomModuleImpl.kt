package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.util.messages.*
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for room actions
 */
class KmeRoomModuleImpl : KmeController(), IKmeRoomModule {

    private val roomApiService: KmeRoomApiService by inject()
    private val messageManager: KmeMessageManager by inject()
    private val webSocketModule: IKmeWebSocketModule by inject()
    private val userController: IKmeUserController by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private val publisherId by lazy { userController.getCurrentUserInfo()?.getUserId() ?: 0 }

    /**
     * Getting all rooms for specific company
     */
    override fun getRooms(
        companyId: Long,
        pages: Long,
        limit: Long,
        success: (response: KmeGetRoomsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRooms(companyId, pages, limit) },
                success,
                error
            )
        }
    }

    /**
     * Getting room info by alias
     */
    override fun getRoomInfo(
        alias: String,
        checkPermission: Int,
        withFiles: Int,
        success: (response: KmeGetRoomInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRoomInfo(alias, withFiles, checkPermission) },
                success,
                error
            )
        }
    }

    /**
     * Joining the room
     */
    override fun joinRoom(roomId: Long, companyId: Long) {
        webSocketModule.send(buildJoinRoomMessage(roomId, companyId))
    }

    /**
     * Joining the room
     */
    override fun joinRoom(roomId: Long, companyId: Long, password: String) {
        webSocketModule.send(buildRoomPasswordMessage(roomId, companyId, password))
    }

    /**
     * Changing setting value for room
     */
    override fun changeRoomSettings(
        roomId: Long,
        userId: Long,
        key: KmePermissionKey,
        value: KmePermissionValue
    ) {
        webSocketModule.send(buildRoomSettingsChangedMessage(roomId, userId, key, value))
    }

    /**
     * Change current room content view
     */
    override fun setActiveContent(view: KmeContentType) {
        // Server side issue
        val userId = if (view == KmeContentType.CONFERENCE_VIEW) {
            null
        } else {
            publisherId
        }
        webSocketModule.send(buildSetActiveContentMessage(userId, view))
    }

    /**
     * Ends active room session
     */
    override fun endSession(roomId: Long, companyId: Long) {
        webSocketModule.send(buildEndSessionMessage(roomId, companyId))
        messageManager.removeListeners()
    }

    /**
     * Ends active room session
     */
    override fun endSessionForEveryone(roomId: Long, companyId: Long) {
        webSocketModule.send(buildEndSessionForEveryoneMessage(roomId, companyId))
        messageManager.removeListeners()
    }

}
