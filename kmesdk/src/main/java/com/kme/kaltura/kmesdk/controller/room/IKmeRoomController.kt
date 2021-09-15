package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
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
    val breakoutModule: IKmeBreakoutModule

    /**
     * Getting WebRTC server data
     */
    val roomSettings: KmeWebRTCServer?

    /**
     * Getting current room metadata
     */
    val roomMetadata: KmeRoomMetaData?

    /**
     * Getting actual room id
     */
    fun getRoomId(): Long

    /**
     * Getting actual company id
     */
    fun getCompanyId(): Long

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

    fun connectToBreakout(
        roomId: Long,
        roomAlias: String,
        listener: IKmeWSConnectionListener
    )

    /**
     * Subscribes to the shared content in the room
     *
     * @param listener content share listener
     */
    fun subscribeForContent(listener: IKmeContentModule.KmeContentListener)

}
