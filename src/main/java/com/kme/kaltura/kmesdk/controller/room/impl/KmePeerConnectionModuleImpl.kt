package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnection
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.*
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType
import org.koin.core.get
import org.koin.core.inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * An implementation for wrap actions with [IKmePeerConnection]
 */
class KmePeerConnectionModuleImpl : KmeController(), IKmePeerConnectionModule {

    private val webSocketModule: IKmeWebSocketModule by inject()
    private val roomController: IKmeRoomController by inject()
    private val userController: IKmeUserController by inject()

    private var publisher: IKmePeerConnection? = null
    private var peerConnections: MutableMap<String, IKmePeerConnection> = mutableMapOf()
    private var payloads: MutableList<SdpOfferToViewerPayload> = mutableListOf()

    private val publisherId: Long by lazy {
        userController.getCurrentUserInfo()?.getUserId() ?: 0
    }

    private var roomId: Long = 0
    private var companyId: Long = 0
    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String
    private lateinit var listener: IKmePeerConnectionModule.KmePeerConnectionEvents
    private var isInitialized: Boolean = false

    /**
     * Setting initialization data to the module
     */
    override fun initialize(
        roomId: Long,
        companyId: Long,
        turnUrl: String,
        turnUser: String,
        turnCred: String,
        listener: IKmePeerConnectionModule.KmePeerConnectionEvents
    ) {
        this.roomId = roomId
        this.companyId = companyId

        this.turnUrl = turnUrl
        this.turnUser = turnUser
        this.turnCred = turnCred

        this.listener = listener
        roomController.listen(
            peerConnectionModuleHandler,
            KmeMessageEvent.SDP_ANSWER_TO_PUBLISHER,
            KmeMessageEvent.SDP_OFFER_FOR_VIEWER,
            KmeMessageEvent.USER_DISCONNECTED
        )
        isInitialized = true
    }

    /**
     * Creates publisher connection
     */
    override fun addPublisher(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView
    ) {
        checkData()

        if (publisher == null) {
            webSocketModule.send(
                buildMediaInitMessage(
                    roomId,
                    companyId,
                    publisherId
                )
            )

            publisher = get()
            publisher?.setTurnServer(turnUrl, turnUser, turnCred)
            publisher?.setLocalRenderer(renderer)
            publisher?.createPeerConnection(true, requestedUserIdStream, this)
            peerConnections[requestedUserIdStream] = publisher!!
        }
    }

    /**
     * Creates a viewer connection
     */
    override fun addViewer(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView
    ) {
        checkData()

        webSocketModule.send(
            buildStartViewingMessage(
                roomId,
                companyId,
                publisherId,
                requestedUserIdStream
            )
        )

        val viewer: IKmePeerConnection by inject()
        viewer.setTurnServer(turnUrl, turnUser, turnCred)
        viewer.setRemoteRenderer(renderer)
        viewer.createPeerConnection(false, requestedUserIdStream, this)
        peerConnections[requestedUserIdStream] = viewer
    }

    /**
     * Getting publishing state
     */
    override fun isPublishing(): Boolean = publisher != null

    /**
     * Toggle publisher's camera
     *
     */
    override fun enableCamera(isEnable: Boolean) {
        sendChangeMediaStateMessage(isEnable, KmeMediaStateType.WEBCAM)
        publisher?.enableCamera(isEnable)
    }

    /**
     * Toggle publisher's audio
     */
    override fun enableAudio(isEnable: Boolean) {
        sendChangeMediaStateMessage(isEnable, KmeMediaStateType.MIC)
        publisher?.enableAudio(isEnable)
    }

    /**
     * Switch between publisher's existing cameras
     */
    override fun switchCamera() {
        publisher?.switchCamera()
    }

    /**
     * Disconnect publisher/viewer connection by id
     */
    override fun disconnect(requestedUserIdStream: String) {
        payloads.find {
            it.requestedUserIdStream == requestedUserIdStream
        }?.let {
            payloads.remove(it)
        }
        peerConnections[requestedUserIdStream]?.disconnectPeerConnection()
        peerConnections.remove(requestedUserIdStream)
        if (publisherId.toString() == requestedUserIdStream) {
            publisher = null
        }
        listener.onPeerConnectionRemoved(requestedUserIdStream)
    }

    /**
     * Disconnect all publisher/viewers connections
     */
    override fun disconnectAll() {
        peerConnections.forEach { (_, connection) -> connection.disconnectPeerConnection() }
        peerConnections.clear()
        payloads.clear()
        publisher = null
    }

    private fun sendChangeMediaStateMessage(enabled: Boolean, device: KmeMediaStateType) {
        checkData()

        webSocketModule.send(
            buildChangeMediaStateMessage(
                roomId,
                companyId,
                publisherId,
                device,
                if (enabled) KmeMediaDeviceState.LIVE else KmeMediaDeviceState.DISABLED_LIVE
            )
        )
    }

    private fun checkData() {
        if (!isInitialized) {
            throw Exception("Module is not initialized")
        }
    }

    private val peerConnectionModuleHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SDP_ANSWER_TO_PUBLISHER -> {
                    val msg: KmeStreamingModuleMessage<SdpAnswerToPublisherPayload>? =
                        message.toType()
                    msg?.payload?.mediaServerId?.let { publisher?.setMediaServerId(it) }
                    msg?.payload?.sdpAnswer?.let { publisher?.setRemoteSdp(KmeSdpType.ANSWER, it) }
                }
                KmeMessageEvent.SDP_OFFER_FOR_VIEWER -> {
                    val msg: KmeStreamingModuleMessage<SdpOfferToViewerPayload>? = message.toType()
                    msg?.payload?.let {
                        payloads.add(it)

                        val id = it.requestedUserIdStream
                        val serverId = it.mediaServerId
                        val sdp = it.sdpOffer
                        if (serverId != null && sdp != null) {
                            peerConnections[id]?.setMediaServerId(serverId)
                            peerConnections[id]?.setRemoteSdp(KmeSdpType.OFFER, sdp)
                        }
                    }
                }
                KmeMessageEvent.USER_DISCONNECTED -> {
                    val msg: KmeStreamingModuleMessage<UserDisconnectedPayload>? = message.toType()
                    msg?.payload?.userId?.toString()?.let { disconnect(it) }
                }
                else -> {
                }
            }
        }
    }

    override fun onPeerConnectionCreated(requestedUserIdStream: String) {
        if (requestedUserIdStream == publisherId.toString()) {
            publisher?.createOffer()
        }
    }

    override fun onLocalDescription(
        requestedUserIdStream: String,
        mediaServerId: Long,
        sdp: String,
        type: String
    ) {
        val msg: KmeMessage<out KmeMessage.Payload>
        if (requestedUserIdStream == publisherId.toString()) {
            msg = buildStartPublishingMessage(
                roomId,
                companyId,
                publisherId,
                type,
                sdp
            )
        } else {
            msg = buildAnswerFromViewerMessage(
                roomId,
                companyId,
                publisherId,
                type,
                sdp,
                requestedUserIdStream,
                mediaServerId
            )
        }
        webSocketModule.send(msg)
    }

    override fun onIceCandidate(candidate: String) {

    }

    override fun onIceCandidatesRemoved(candidates: String) {

    }

    override fun onIceConnected() {

    }

    override fun onIceGatheringDone(requestedUserIdStream: String, mediaServerId: Long) {
        val msg: KmeMessage<out KmeMessage.Payload>
        if (requestedUserIdStream == publisherId.toString()) {
            listener.onPublisherReady()

            msg = buildGatheringPublishDoneMessage(
                roomId,
                companyId,
                publisherId,
                mediaServerId
            )
        } else {
            listener.onViewerReady(requestedUserIdStream)

            msg = buildGatheringViewDoneMessage(
                roomId,
                companyId,
                publisherId,
                requestedUserIdStream,
                mediaServerId
            )
        }
        webSocketModule.send(msg)
    }

    override fun onUserSpeaking(requestedUserIdStream: String, isSpeaking: Boolean) {
        listener.onUserSpeaking(requestedUserIdStream, isSpeaking)
    }

    override fun onIceDisconnected() {

    }

    override fun onPeerConnectionClosed() {

    }

    override fun onPeerConnectionStatsReady(reports: String) {

    }

    override fun onPeerConnectionError(requestedUserIdStream: String, description: String) {
        listener.onPeerConnectionError(requestedUserIdStream, description)
    }

}
