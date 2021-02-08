package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

/**
 * An interface for room actions
 */
interface IKmeRoomModule {

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

}
