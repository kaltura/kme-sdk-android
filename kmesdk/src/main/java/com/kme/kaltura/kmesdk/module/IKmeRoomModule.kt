package com.kme.kaltura.kmesdk.module

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.*
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeRoomExitReason
import com.kme.kaltura.kmesdk.ws.message.room.KmeRoomMetaData
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.KmeXlRoomStatus
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

/**
 * An interface for room actions
 */
interface IKmeRoomModule : IKmeModule {

    /**
     * Subscribing for the room events
     */
    fun subscribe()

    /**
     * Setting listener for basic room states
     */
    fun setRoomStateListener(stateListener: IKmeRoomStateListener?)

    /**
     * Getting listener for basic room states
     */
    fun getRoomStateListener() : IKmeRoomStateListener?

    /**
     * Getting current room id
     *
     * @return the current room id
     * */
    fun getCurrentRoomId() : Long

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
     * Getting main room id
     */
    fun getMainRoomId() : Long

    /**
    * Getting main room alias
     */
    fun getMainRoomAlias() : String

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

    interface IKmeRoomStateListener {

        fun onRoomAvailable(room: KmeRoomMetaData)

        fun onRoomBanner(
            message: KmeMessage<KmeMessage.Payload>
        )

        fun onRoomTerms(type: IKmeTermsModule.KmeTermsType, message: String? = null)

        fun onXLRoom(status: KmeXlRoomStatus)

        fun onRoomExit(reason: KmeRoomExitReason)

        fun onRoomUnavailable(throwable: Throwable?)

    }

}
