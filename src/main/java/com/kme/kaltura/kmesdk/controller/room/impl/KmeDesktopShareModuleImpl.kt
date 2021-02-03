package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule.*
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildAnswerFromViewerMessage
import com.kme.kaltura.kmesdk.util.messages.buildDesktopShareInitOnRoomInitMessage
import com.kme.kaltura.kmesdk.util.messages.buildGatheringViewDoneMessage
import com.kme.kaltura.kmesdk.util.messages.buildStartViewingMessage
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeSdpType
import org.koin.core.inject

/**
 * An implementation for desktop share actions
 */
class KmeDesktopShareModuleImpl : KmeController(), IKmeDesktopShareModule,
    IKmePeerConnectionClientEvents {

    private val roomController: IKmeRoomController by inject()
    private val userController: IKmeUserController by inject()
    private val webSocketModule: IKmeWebSocketModule by inject()

    private var roomId: Long = 0
    private var companyId: Long = 0
    private lateinit var renderer: KmeSurfaceRendererView
    private lateinit var callback: IKmeDesktopShareEvents

    /**
     * Start listen desktop share events
     */
    override fun startListenDesktopShare(
        roomId: Long,
        companyId: Long,
        renderer: KmeSurfaceRendererView,
        callback: IKmeDesktopShareEvents
    ) {
        this.roomId = roomId
        this.companyId = companyId
        this.renderer = renderer
        this.callback = callback

        roomController.listen(
            desktopShareHandler,
            KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.SDP_OFFER_FOR_VIEWER,
            KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED
        )
        webSocketModule.send(buildDesktopShareInitOnRoomInitMessage(roomId, companyId))
    }

    /**
     * Stop listen desktop share events
     */
    override fun stopListenDesktopShare() {
        roomController.removeListener(desktopShareHandler)
    }

    /**
     * Handler for WS desktop share events
     */
    private val desktopShareHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.DESKTOP_SHARE_STATE_UPDATED -> {
                    val msg: KmeDesktopShareModuleMessage<DesktopShareStateUpdatedPayload>? =
                        message.toType()
                    val isActive = msg?.payload?.isActive
                    val onRoomInit = msg?.payload?.onRoomInit

                    if (isActive != null) {
                        callback.onDesktopShareActive(isActive)
                    }

                    if (isActive == true && onRoomInit == true) {
                        sendStartViewMessage("${msg.payload?.userId}_desk")
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()
                    msg?.payload?.userId?.let { requestedUserIdStream ->
                        if (requestedUserIdStream.toLongOrNull() == null) {
                            sendStartViewMessage(requestedUserIdStream)
                        }
                    }
                }
                KmeMessageEvent.SDP_OFFER_FOR_VIEWER -> {
                    val msg: KmeStreamingModuleMessage<SdpOfferToViewerPayload>? = message.toType()
                    msg?.payload?.let { payload ->
                        payload.requestedUserIdStream?.let { streamId ->
                            if (streamId.toLongOrNull() == null) {
                                val mediaServerId = payload.mediaServerId
                                val description = payload.sdpOffer
                                if (mediaServerId != null && description != null) {
                                    roomController.peerConnectionModule.disconnectPeerConnection(streamId)

                                    roomController.peerConnectionModule.addViewerPeerConnection(
                                        streamId,
                                        renderer,
                                        this@KmeDesktopShareModuleImpl
                                    )?.apply {
                                        setMediaServerId(mediaServerId)
                                        setRemoteSdp(KmeSdpType.OFFER, description)
                                    }
                                }
                            }
                        }
                    }
                }
                KmeMessageEvent.DESKTOP_SHARE_QUALITY_UPDATED -> {
                    val msg: KmeDesktopShareModuleMessage<DesktopShareQualityUpdatedPayload>? =
                        message.toType()
                    msg?.payload?.isHD?.let {
                        callback.onDesktopShareQualityChanged(it)
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun sendStartViewMessage(requestedUserIdStream: String) {
        webSocketModule.send(
            buildStartViewingMessage(
                roomId,
                companyId,
                userController.getCurrentUserInfo()?.getUserId() ?: 0,
                requestedUserIdStream
            )
        )
    }

    override fun onPeerConnectionCreated(requestedUserIdStream: String) {

    }

    override fun onLocalDescription(
        requestedUserIdStream: String,
        mediaServerId: Long,
        sdp: String,
        type: String
    ) {
        webSocketModule.send(
            buildAnswerFromViewerMessage(
                roomId,
                companyId,
                userController.getCurrentUserInfo()?.getUserId() ?: 0,
                type,
                sdp,
                requestedUserIdStream,
                mediaServerId
            )
        )
    }

    override fun onIceCandidate(candidate: String) {

    }

    override fun onIceCandidatesRemoved(candidates: String) {

    }

    override fun onIceConnected() {

    }

    override fun onIceGatheringDone(requestedUserIdStream: String, mediaServerId: Long) {
        webSocketModule.send(
            buildGatheringViewDoneMessage(
                roomId,
                companyId,
                userController.getCurrentUserInfo()?.getUserId() ?: 0,
                requestedUserIdStream,
                mediaServerId
            )
        )
    }

    override fun onUserSpeaking(requestedUserIdStream: String, isSpeaking: Boolean) {

    }

    override fun onIceDisconnected() {

    }

    override fun onPeerConnectionClosed() {

    }

    override fun onPeerConnectionStatsReady(reports: String) {

    }

    override fun onPeerConnectionError(description: String) {

    }

}
