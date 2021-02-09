package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule.KmeDesktopShareEvents
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.messages.buildDesktopShareInitOnRoomInitMessage
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareQualityUpdatedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeDesktopShareModuleMessage.DesktopShareStateUpdatedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.StartedPublishPayload
import org.koin.core.inject

/**
 * An implementation for desktop share actions
 */
class KmeDesktopShareModuleImpl : KmeController(), IKmeDesktopShareModule {

    private val roomController: IKmeRoomController by inject()
    private val webSocketModule: IKmeWebSocketModule by inject()

    private var roomId: Long = 0
    private var companyId: Long = 0
    private lateinit var renderer: KmeSurfaceRendererView
    private lateinit var callback: KmeDesktopShareEvents

    /**
     * Start listen desktop share events
     */
    override fun startListenDesktopShare(
        roomId: Long,
        companyId: Long,
        renderer: KmeSurfaceRendererView,
        callback: KmeDesktopShareEvents
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
                        roomController.peerConnectionModule.addViewer(
                            "${msg.payload?.userId}_desk",
                            renderer
                        )
                    }
                }
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()
                    msg?.payload?.userId?.let {
                        if (it.toLongOrNull() == null) {
                            roomController.peerConnectionModule.disconnect(it)
                            roomController.peerConnectionModule.addViewer(it, renderer)
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

}
