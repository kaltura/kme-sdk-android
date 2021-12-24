package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer
import com.kme.kaltura.kmesdk.ws.IKmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.room.KmeRoomMetaData

/**
 * An interface for room data
 */
interface IKmeRoomController : IKmeWebSocketModule, IKmeMessageManager {

    val roomModule: IKmeRoomModule
    val peerConnectionModule: IKmePeerConnectionModule
    val participantModule: IKmeParticipantModule
    val chatModule: IKmeChatModule
    val noteModule: IKmeNoteModule
    val recordingModule: IKmeRecordingModule
    val audioModule: IKmeAudioModule
    val termsModule: IKmeTermsModule
    val breakoutModule: IKmeBreakoutModule

    /**
     * Getting WebRTC server data
     */
    val webRTCServer: KmeWebRTCServer?

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
     * @param roomStateListener exit room listener
//     * @param listener connection listener
     */
    fun connect(
        roomId: Long,
        roomAlias: String,
        companyId: Long,
        isReconnect: Boolean = true,
        roomStateListener: IKmeRoomModule.IKmeRoomStateListener,
//        listener: IKmeWSConnectionListener
    )

    fun connectToBreakout(
        roomId: Long,
        roomAlias: String,
        roomStateListener: IKmeRoomModule.IKmeRoomStateListener
    )

    /**
     * Subscribes to the shared content in the room
     *
     * @param listener content share listener
     */
    fun subscribeForContent(listener: IKmeContentModule.KmeContentListener)

}
