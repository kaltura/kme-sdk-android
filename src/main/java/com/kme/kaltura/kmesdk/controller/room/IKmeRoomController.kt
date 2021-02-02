package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent

/**
 * An interface for room data
 */
interface IKmeRoomController : IKmeWebSocketModule, IKmeWebRTCModule {

    val roomModule: IKmeRoomModule
    val participantModule: IKmeParticipantModule
    val chatModule: IKmeChatModule
    val noteModule: IKmeNoteModule
    val recordingModule: IKmeRecordingModule
    val desktopShareModule: IKmeDesktopShareModule
    val audioModule: IKmeAudioModule

    /**
     * Getting WevRTC server data if exist
     */
    val roomSettings: KmeWebRTCServer?

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
