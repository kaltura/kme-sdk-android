package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.room.KmeRoomMetaData

/**
 * An interface for room data
 */
interface IKmeRoomController : IKmeWebSocketModule {

    val roomModule: IKmeRoomModule
    val peerConnectionModule: IKmePeerConnectionModule
    val participantModule: IKmeParticipantModule
    val chatModule: IKmeChatModule
    val noteModule: IKmeNoteModule
    val recordingModule: IKmeRecordingModule
    val audioModule: IKmeAudioModule

    /**
     * Getting WebRTC server data
     */
    val roomSettings: KmeWebRTCServer?


    /**
     * Getting current room metadata
     */
    val roomMetadata: KmeRoomMetaData?

    /**
     * Connect to the room via web socket. Update actual user information first.
     *
     * @param roomId id of a room
     * @param roomAlias alias of a room
     * @param companyId alias of a company
     * @param isReconnect reconnection flag
     * @param listener connection listener
     */
    fun connect(
        roomId: Long,
        roomAlias: String,
        companyId: Long,
        isReconnect: Boolean = true,
        listener: IKmeWSConnectionListener
    )

    /**
     * Subscribes to the shared content in the room
     *
     * @param listener content share listener
     */
    fun subscribeForContent(listener: IKmeContentModule.KmeContentListener)

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
