package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

/**
 * An interface for room actions
 */
interface IKmeRoomModule : IKmeModule {

    /**
     * Getting all rooms for specific company
     *
     * @param companyId id of a company
     * @param pages page number
     * @param limit count of rooms per page
     * @param success function to handle success result. Contains [KmeGetRoomsResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getRooms(
        companyId: Long,
        pages: Long,
        limit: Long,
        success: (response: KmeGetRoomsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Handling cookies for login via deep linking
     *
     * @param hash identifier for a user
     * @param success function to handle success result. Contains [KmeJoinRoomResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun joinWithHash(
        hash: String,
        success: (response: KmeJoinRoomResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting room info by alias
     *
     * @param alias alias of a room
     * @param checkPermission
     * @param withFiles
     * @param success function to handle success result. Contains [KmeGetRoomInfoResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getRoomInfo(
        alias: String,
        checkPermission: Int,
        withFiles: Int,
        success: (response: KmeGetRoomInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Joining the room
     *
     * @param roomId alias of a room
     * @param companyId id of a company
     */
    fun joinRoom(
        roomId: Long,
        companyId: Long
    )

    /**
     * Joining the room
     *
     * @param roomId id of a room
     * @param companyId id of a company
     * @param password password to joining to the room
     */
    fun joinRoom(
        roomId: Long,
        companyId: Long,
        password: String
    )

    /**
     * Changing setting value for room
     *
     * @param roomId id of a room
     * @param userId id of a user
     * @param key key of setting to set
     * @param value new value of a setting
     */
    fun changeRoomSettings(
        roomId: Long,
        userId: Long,
        key: KmePermissionKey,
        value: KmePermissionValue
    )

    /**
     * Change current room content view
     *
     * @param view content type to set
     */
    fun setActiveContent(view: KmeContentType)

    /**
     * Subscribes to the shared content in the room
     *
     * @param listener content share listener
     */
    fun subscribeForContent(listener: IKmeContentModule.KmeContentListener)

    /**
     * Mute/Un-mute presented audio
     *
     * @param isMute
     */
    fun muteActiveContent(isMute: Boolean)
    
    /**
     * Ends active room session
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun endSession(
        roomId: Long,
        companyId: Long
    )

    /**
     * Ends active room session
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun endSessionForEveryone(
        roomId: Long,
        companyId: Long
    )

    /**
     * Getting actual state for xl room
     *
     * @param roomId id of a room
     * @param companyId id of a company
     */
    fun getXlRoomState(
        roomId: Long,
        companyId: Long
    )

    /**
     * Start initiating xl room
     */
    fun prepareXlRoom(
        roomId: Long,
        userId: Long,
        participants: Int,
        presenters: Int,
        regionId: String,
        global: Boolean = false,
        success: (response: KmeXlRoomPrepareResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Launch xl room
     */
    fun launchXlRoom(
        roomId: Long,
        success: (response: KmeXlRoomLaunchResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Stop xl room initiation
     */
    fun stopXlRoom(
        roomId: Long,
        companyId: Long,
        success: (response: KmeXlRoomStopResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
