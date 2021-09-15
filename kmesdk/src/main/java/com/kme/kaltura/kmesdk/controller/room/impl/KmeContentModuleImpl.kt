package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareFragment
import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareViewModel
import com.kme.kaltura.kmesdk.content.playkit.KmeMediaContentFragment
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentFragment
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentViewModel
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardContentViewModel
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeContentModule
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.inject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType.*

/**
 * An implementation for content sharing
 */
internal class KmeContentModuleImpl : KmeController(), IKmeContentModule {

    private val roomController: IKmeRoomController by controllersScope().inject()
    private val peerConnectionModule: IKmePeerConnectionModule by modulesScope().inject()
    private val slidesContentViewModel: KmeSlidesContentViewModel by viewModelsScope().inject()
    private val whiteboardViewModel: KmeWhiteboardContentViewModel by viewModelsScope().inject()
    private val desktopShareViewModel: KmeDesktopShareViewModel by viewModelsScope().inject()

    private var contentView: KmeContentView? = null
    private var listener: IKmeContentModule.KmeContentListener? = null

    /**
     * Subscribing for the room events related to content sharing
     */
    override fun subscribe(listener: IKmeContentModule.KmeContentListener) {
        this.listener = listener

        slidesContentViewModel.subscribe()
        whiteboardViewModel.subscribe()

        roomController.listen(
            activeContentHandler,
            KmeMessageEvent.INIT_ACTIVE_CONTENT,
            KmeMessageEvent.SET_ACTIVE_CONTENT
        )
    }

    /**
     * Un-subscribing from the room events related to content sharing
     */
    override fun unsubscribe() {
        roomController.removeListener(activeContentHandler)
    }

    /**
     * Asking content view for screen share renderer. Fired once KmeSDK needs view to render own screen
     */
    override fun askForScreenShareRenderer(callback: (view: KmeSurfaceRendererView) -> Unit) {
        if (contentView is KmeDesktopShareFragment) {
            (contentView as KmeDesktopShareFragment).onGetRenderer(callback)
        }
    }

    /**
     * Setting result of screen projection permission from MediaProjectionManager
     * Fired once application provides permission result to the KmeSDK
     */
    override fun onScreenSharePermission(approved: Boolean) {
        if (contentView is KmeDesktopShareFragment) {
            (contentView as KmeDesktopShareFragment).onScreenSharePermission(approved)
        }
    }

    private val activeContentHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.INIT_ACTIVE_CONTENT,
                KmeMessageEvent.SET_ACTIVE_CONTENT -> {
                    val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? =
                        message.toType()
                    contentMessage?.payload?.let {
                        setActiveContent(it)
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * Setting actual shared content
     */
    fun setActiveContent(payload: SetActiveContentPayload) {
        val contentType = payload.contentType
        if (contentType != null) {
            when (contentType) {
                VIDEO, AUDIO, YOUTUBE, KALTURA -> {
                    contentView = KmeMediaContentFragment.newInstance(payload)
                }
                IMAGE, SLIDES -> {
                    contentView = KmeSlidesContentFragment.newInstance(payload)
                }
                WHITEBOARD -> {
                    if (!payload.metadata.pages.isNullOrEmpty()) {
                        contentView = KmeSlidesContentFragment.newInstance(payload)
                    }
                }
                DESKTOP_SHARE -> {
                    desktopShareViewModel.subscribe()
                    contentView = KmeDesktopShareFragment.newInstance()
                }
                else -> {
                    if (contentView is KmeDesktopShareFragment) {
                        peerConnectionModule.stopScreenShare()
                    }
                    contentView = null
                }
            }
            contentView?.let {
                listener?.onContentAvailable(it)
            } ?: run {
                listener?.onContentNotAvailable()
            }
        }
    }

}
