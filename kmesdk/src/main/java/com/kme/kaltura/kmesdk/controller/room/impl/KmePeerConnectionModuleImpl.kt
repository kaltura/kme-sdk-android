package com.kme.kaltura.kmesdk.controller.room.impl

import android.app.Activity
import android.content.Intent
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.*
import com.kme.kaltura.kmesdk.controller.room.internal.IKmePeerConnectionInternalModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.*
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnection
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.UserMediaStateChangedPayload
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
import kotlin.properties.Delegates

/**
 * An implementation for wrap actions with [IKmePeerConnection]
 */
class KmePeerConnectionModuleImpl : KmeController(), IKmePeerConnectionModule,
    IKmePeerConnectionInternalModule {

    private val userController: IKmeUserController by inject()
    private val roomController: IKmeRoomController by scopedInject()
    private val webSocketModule: IKmeWebSocketModule by scopedInject()
    private val contentModule: IKmeContentModule by scopedInject()

    private var preview: IKmePeerConnection? = null
    private var publisher: IKmePeerConnection? = null
    private var screenSharer: IKmePeerConnection? = null
    private var viewers: MutableMap<String, IKmePeerConnection> = mutableMapOf()
    private var payloads: MutableList<SdpOfferToViewerPayload> = mutableListOf()

    private var publisherId by Delegates.notNull<Long>()
    private var useWsEvents by Delegates.notNull<Boolean>()
    private var bringToFrontPrev = 0

    private var roomId: Long = 0
    private var companyId: Long = 0
    private lateinit var turnUrl: String
    private lateinit var turnUser: String
    private lateinit var turnCred: String
    private lateinit var listener: IKmePeerConnectionModule.KmePeerConnectionEvents
    private var screenShareEvents: IKmePeerConnectionModule.KmeScreenShareEvents? = null

    private var viewersAudioEnabledByApp = true
    private var viewersAudioEnabledBySdk = true
    private var isInitialized: Boolean = false
    private var blockMediaStateEvents: Boolean = false

    /**
     * Setting initialization data to the module
     */
    override fun initialize(
        roomId: Long,
        companyId: Long,
        listener: IKmePeerConnectionModule.KmePeerConnectionEvents,
    ) {
        this.roomId = roomId
        this.companyId = companyId

        publisherId = userController.getCurrentUserInfo()?.getUserId() ?: 0
        useWsEvents = roomController.roomSettings?.featureFlags?.nr2DataChannelViaRs ?: true

        val turnUrl = roomController.roomSettings?.turnUrl
        val turnUser = roomController.roomSettings?.turnUsername
        val turnCred = roomController.roomSettings?.turnCredential

        if (turnUrl != null && turnUser != null && turnCred != null) {
            this.turnUrl = turnUrl
            this.turnUser = turnUser
            this.turnCred = turnCred

            this.listener = listener
            roomController.listen(
                peerConnectionModuleHandler,
                KmeMessageEvent.SDP_ANSWER_TO_PUBLISHER,
                KmeMessageEvent.SDP_OFFER_FOR_VIEWER,
                KmeMessageEvent.USER_DISCONNECTED,
                KmeMessageEvent.USER_MEDIA_STATE_CHANGED,
                KmeMessageEvent.USER_SPEAKING
            )
            blockMediaStateEvents = false
            isInitialized = true
        }
    }

    /**
     * Setting initialization data to the module
     */
    override fun initialize(
        roomId: Long,
        companyId: Long,
        listener: IKmePeerConnectionModule.KmePeerConnectionEvents,
        screenShareEvents: IKmePeerConnectionModule.KmeScreenShareEvents,
    ) {
        this.screenShareEvents = screenShareEvents

        initialize(roomId, companyId, listener)
    }

    /**
     * Creates a video preview
     */
    override fun startPreview(previewRenderer: KmeSurfaceRendererView) {
        if (preview == null) {
            preview = get()
            preview?.startPreview(previewRenderer)
        }
    }

    /**
     * Stops a video preview
     */
    override fun stopPreview() {
        preview?.disconnectPeerConnection()
        preview = null
    }

    override fun startLive(
        requestedUserIdStream: String,
        liveState: KmeMediaDeviceState,
        micState: KmeMediaDeviceState,
        camState: KmeMediaDeviceState,
    ) {
        webSocketModule.send(
            buildMediaInitMessage(
                roomId,
                companyId,
                publisherId,
                liveState,
                micState,
                camState
            )
        )
    }

    /**
     * Creates publisher connection
     */
    override fun addPublisherConnection(
        requestedUserIdStream: String,
        liveState: KmeMediaDeviceState,
        micState: KmeMediaDeviceState,
        camState: KmeMediaDeviceState,
        frontCamEnabled: Boolean,
    ) {
        checkData()

        publisher?.let { return }

        webSocketModule.send(
            buildMediaInitMessage(
                roomId,
                companyId,
                publisherId,
                liveState,
                micState,
                camState
            )
        )

        publisher = get()
        publisher?.apply {
            setTurnServer(turnUrl, turnUser, turnCred)
            setPreferredSettings(
                micState == KmeMediaDeviceState.LIVE,
                camState == KmeMediaDeviceState.LIVE,
                frontCamEnabled
            )
            createPeerConnection(
                requestedUserIdStream,
                isPublisher = true,
                !useWsEvents,
                this@KmePeerConnectionModuleImpl
            )
        }
    }

    /**
     * Creates a viewer connection
     */
    override fun addViewerConnection(requestedUserIdStream: String) {
        checkData()

        viewers[requestedUserIdStream]?.let { return }

        webSocketModule.send(
            buildStartViewingMessage(
                roomId,
                companyId,
                publisherId,
                requestedUserIdStream
            )
        )

        val viewer: IKmePeerConnection by inject()
        viewer.apply {
            setTurnServer(turnUrl, turnUser, turnCred)
            createPeerConnection(
                requestedUserIdStream,
                isPublisher = false,
                !useWsEvents,
                this@KmePeerConnectionModuleImpl
            )
        }
        viewers[requestedUserIdStream] = viewer
    }

    override fun setPublisherRenderer(renderer: KmeSurfaceRendererView) {
        publisher?.setRenderer(renderer)
    }

    override fun setViewerRenderer(
        requestedUserIdStream: String,
        renderer: KmeSurfaceRendererView,
    ) {
        viewers[requestedUserIdStream]?.setRenderer(renderer)
    }

    override fun removePublisherRenderer() {
        checkData()
        publisher?.removeRenderer()
    }

    override fun removeViewerRenderer(requestedUserIdStream: String) {
        checkData()
        viewers[requestedUserIdStream]?.removeRenderer()
    }

    /**
     * Getting publishing state
     */
    override fun isPublishing() = publisher != null

    /**
     * Asking for screen permission from MediaProjectionManager
     */
    override fun askForScreenSharePermission() {
        screenShareEvents?.onAskForScreenSharePermission()
    }

    /**
     * Set status from MediaProjectionManager
     */
    override fun setScreenSharePermission(
        resultCode: Int,
        screenCaptureIntent: Intent,
    ) {
        val approved = resultCode == Activity.RESULT_OK
        contentModule.onScreenSharePermission(approved)

        if (!approved) { return }

        screenSharer?.let { return }

        webSocketModule.send(
            buildUpdateDesktopShareStateMessage(
                roomId,
                publisherId.toString(),
                companyId,
                true
            )
        )

        screenSharer = get()
        screenSharer?.setTurnServer(turnUrl, turnUser, turnCred)
        screenSharer?.startScreenShare(
            "${publisherId}_desk",
            screenCaptureIntent,
            this
        )
    }

    /**
     * Stops screen share publishing
     */
    override fun stopScreenShare() {
        checkData()
        screenSharer?.let {
            webSocketModule.send(
                buildUpdateDesktopShareStateMessage(
                    roomId,
                    "${publisherId}_desk",
                    companyId,
                    false
                )
            )

            it.disconnectPeerConnection()
            screenSharer = null
        }
    }

    override fun setScreenShareRenderer(renderer: KmeSurfaceRendererView) {
        checkData()
        screenSharer?.let {
            it.setRenderer(renderer)
        }
    }

    /**
     * Toggle publisher's camera
     *
     */
    override fun enableCamera(
        isEnable: Boolean,
        silent: Boolean
    ) {
        publisher?.let {
            if (blockMediaStateEvents) return
            if (!silent) {
                sendChangeMediaStateMessage(isEnable, KmeMediaStateType.WEBCAM)
            }
            it.enableCamera(isEnable)
        }
        preview?.enableCamera(isEnable)
    }

    /**
     * Toggle publisher's audio
     */
    override fun enableAudio(
        isEnable: Boolean,
        silent: Boolean
    ) {
        if (blockMediaStateEvents) return
        if (!silent) {
            sendChangeMediaStateMessage(isEnable, KmeMediaStateType.MIC)
        }
        publisher?.enableAudio(isEnable)
    }

    override fun enableViewersAudio(isEnable: Boolean) {
        if (viewersAudioEnabledBySdk) {
            checkData()
            viewersAudioEnabledByApp = isEnable
            viewers.forEach { (_, connection) -> connection.enableAudio(isEnable) }
        }
    }

    /**
     * Switch between publisher's existing cameras
     */
    override fun switchCamera() {
        preview?.switchCamera()
        publisher?.switchCamera()
    }

    /**
     * Disconnect publisher/viewer connection by id
     */
    override fun disconnect(requestedUserIdStream: String) {
        if (publisherId.toString() == requestedUserIdStream) {
            preview?.disconnectPeerConnection()
            preview = null
            publisher?.disconnectPeerConnection()
            publisher = null
        } else {
            payloads.find {
                it.requestedUserIdStream == requestedUserIdStream
            }?.let {
                payloads.remove(it)
            }
            viewers[requestedUserIdStream]?.disconnectPeerConnection()
            viewers.remove(requestedUserIdStream)
        }
    }

    /**
     * Disconnect all publisher/viewers connections
     */
    override fun disconnectAll() {
        viewers.forEach { (_, connection) -> connection.disconnectPeerConnection() }
        viewers.clear()
        payloads.clear()

        preview?.disconnectPeerConnection()
        preview = null
        publisher?.disconnectPeerConnection()
        publisher = null
        screenSharer?.disconnectPeerConnection()
        screenSharer = null
    }

    private fun sendChangeMediaStateMessage(enabled: Boolean, device: KmeMediaStateType) {
        checkData()

        blockMediaStateEvents = true
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

                    val userId = msg?.payload?.userId
                    val mediaServerId = msg?.payload?.mediaServerId
                    val sdpAnswer = msg?.payload?.sdpAnswer

                    if (mediaServerId != null && sdpAnswer != null) {
                        if (userId == publisherId.toString()) {
                            publisher?.setMediaServerId(mediaServerId)
                            publisher?.setRemoteSdp(KmeSdpType.ANSWER, sdpAnswer)
                        } else {
                            screenSharer?.setMediaServerId(mediaServerId)
                            screenSharer?.setRemoteSdp(KmeSdpType.ANSWER, sdpAnswer)
                        }
                    }
                }
                KmeMessageEvent.SDP_OFFER_FOR_VIEWER -> {
                    val msg: KmeStreamingModuleMessage<SdpOfferToViewerPayload>? = message.toType()
                    msg?.payload?.let {
                        payloads.add(it)

                        val id = it.requestedUserIdStream
                        val serverId = it.mediaServerId
                        val sdp = it.sdpOffer
                        if (serverId != null && sdp != null) {
                            viewers[id]?.setMediaServerId(serverId)
                            viewers[id]?.setRemoteSdp(KmeSdpType.OFFER, sdp)
                        }
                    }
                }
                KmeMessageEvent.USER_DISCONNECTED -> {
                    val msg: KmeStreamingModuleMessage<UserDisconnectedPayload>? = message.toType()

                    msg?.payload?.userId?.toString()?.let { disconnect(it) }
                }
                KmeMessageEvent.USER_SPEAKING -> {
                    val msg: KmeStreamingModuleMessage<UserSpeakingPayload>? = message.toType()

                    val userId = msg?.payload?.userId
                    val volumeData = msg?.payload?.volumeData?.split(",")
                    if (userId != null && volumeData != null) {
                        val isSpeaking = volumeData[0].toInt() == 1
                        if (!viewersAudioEnabledBySdk && isSpeaking) return
                        listener.onUserSpeaking(userId.toString(), isSpeaking)
                    }
                }
                KmeMessageEvent.USER_MEDIA_STATE_CHANGED -> {
                    val msg: KmeParticipantsModuleMessage<UserMediaStateChangedPayload>? =
                        message.toType()

                    msg?.payload?.userId?.let {
                        if (it == publisherId) {
                            blockMediaStateEvents = false
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onPeerConnectionCreated(requestedUserIdStream: String) {
        when (requestedUserIdStream) {
            publisherId.toString() -> {
                publisher?.createOffer()
            }
            "${publisherId}_desk" -> {
                screenSharer?.createOffer()
            }
            else -> {
            }
        }
    }

    override fun onLocalDescription(
        requestedUserIdStream: String,
        mediaServerId: Long,
        sdp: String,
        type: String,
    ) {
        val msg: KmeMessage<out KmeMessage.Payload>
        when (requestedUserIdStream) {
            publisherId.toString() -> {
                listener.onPublisherReady()

                msg = buildStartPublishingMessage(
                    roomId,
                    companyId,
                    publisherId,
                    type,
                    sdp
                )
            }
            "${publisherId}_desk" -> {
                msg = buildStartScreenShareMessage(
                    roomId,
                    companyId,
                    publisherId,
                    type,
                    sdp
                )
            }
            else -> {
                listener.onViewerReady(requestedUserIdStream)

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
        when (requestedUserIdStream) {
            publisherId.toString() -> {
                msg = buildGatheringPublishDoneMessage(
                    roomId,
                    companyId,
                    publisherId,
                    mediaServerId,
                    false
                )
            }
            "${publisherId}_desk" -> {
                msg = buildGatheringPublishDoneMessage(
                    roomId,
                    companyId,
                    publisherId,
                    mediaServerId,
                    true
                )
            }
            else -> {
                msg = buildGatheringViewDoneMessage(
                    roomId,
                    companyId,
                    publisherId,
                    requestedUserIdStream,
                    mediaServerId
                )
            }
        }
        webSocketModule.send(msg)
    }

    override fun onUserSpeaking(requestedUserIdStream: String, amplitude: Int) {
        val bringToFront = if (amplitude < VALUE_TO_DETECT) 0 else 1
        val volumeData = "$bringToFront,$amplitude"

        if (bringToFront == bringToFrontPrev) return
        bringToFrontPrev = bringToFront

        if (!viewersAudioEnabledBySdk && bringToFront == 1) return

        if (publisherId.toString() == requestedUserIdStream) {
            webSocketModule.send(
                buildUserSpeakingMessage(
                    roomId,
                    companyId,
                    publisherId,
                    volumeData
                )
            )
        }
        listener.onUserSpeaking(requestedUserIdStream, bringToFront == 1)
    }

    override fun onIceDisconnected() {

    }

    override fun onPeerConnectionClosed(requestedUserIdStream: String) {
        listener.onPeerConnectionRemoved(requestedUserIdStream)
    }

    override fun onPeerConnectionStatsReady(reports: String) {

    }

    override fun onPeerConnectionError(requestedUserIdStream: String, description: String) {
        listener.onPeerConnectionError(requestedUserIdStream, description)
    }

    override fun enableViewersAudioInternal(isEnable: Boolean) {
        if (viewersAudioEnabledByApp) {
            checkData()
            viewersAudioEnabledBySdk = isEnable
            viewers.forEach { (_, connection) -> connection.enableAudio(isEnable) }
            publisher?.enableAudioInternal(isEnable)
        }
    }

    companion object {
        private const val VALUE_TO_DETECT = 150
    }

}
