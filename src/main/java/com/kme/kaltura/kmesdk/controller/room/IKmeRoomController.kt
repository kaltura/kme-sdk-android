package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent

/**
 * An interface for room data
 */
interface IKmeRoomController : IKmeWebSocketModule, IKmeWebRTCModule {

    val chatModule: IKmeChatModule
    val noteModule: IKmeNoteModule
    val recordingModule: IKmeRecordingModule
    val audioModule: IKmeAudioModule

    /**
     * Getting WevRTC server data if exist
     */
    val roomSettings: KmeWebRTCServer?

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
     * Getting data for p2p connection
     *
     * @param roomAlias alias of a room
     * @param success function to handle success result. Contains [KmeGetWebRTCServerResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getWebRTCLiveServer(
        roomAlias: String,
        success: (response: KmeGetWebRTCServerResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Add listeners for socket messages
     *
     * @param listener listener for messages
     */
    fun addListener(listener: IKmeMessageListener)

    /**
     * Add event to listener
     *
     * @param event event to listen
     * @param listener listener for messages
     */
    fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener
    )

    /**
     * Start listen events for listener
     *
     * @param listener listener for messages
     * @param events events to listen
     */
    fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    )

    /**
     * Stop listen events for listener
     *
     * @param listener listener for messages
     * @param events events to stop listen
     */
    fun remove(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    )

    /**
     * Remove listener
     *
     * @param listener listener for messages
     */
    fun removeListener(listener: IKmeMessageListener)

    /**
     * Remove all attached listeners
     */
    fun removeListeners()

}
