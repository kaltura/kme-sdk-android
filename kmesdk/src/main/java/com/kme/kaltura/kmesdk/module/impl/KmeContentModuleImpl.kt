package com.kme.kaltura.kmesdk.module.impl

import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareFragment
import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareViewModel
import com.kme.kaltura.kmesdk.content.playkit.KmeMediaContentFragment
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentFragment
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentViewModel
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardContentViewModel
import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.module.IKmeContentModule
import com.kme.kaltura.kmesdk.module.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessageFilter
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType.*

/**
 * An implementation for content sharing
 */
internal class KmeContentModuleImpl : KmeController(), IKmeContentModule {

    private val roomController: IKmeRoomController by scopedInject()
    private val peerConnectionModule: IKmePeerConnectionModule by scopedInject()
    private val slidesContentViewModel: KmeSlidesContentViewModel by scopedInject()
    private val whiteboardViewModel: KmeWhiteboardContentViewModel by scopedInject()
    private val desktopShareViewModel: KmeDesktopShareViewModel by scopedInject()

    private var contentView: KmeContentView? = null
    private var listener: IKmeContentModule.KmeContentListener? = null
    private var savedMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? = null
    private var isMuted = false

    override fun subscribe() {
        slidesContentViewModel.subscribe()
        whiteboardViewModel.subscribe()

        roomController.listen(
            activeContentHandler,
            KmeMessageEvent.INIT_ACTIVE_CONTENT,
            KmeMessageEvent.SET_ACTIVE_CONTENT,
            priority = KmeMessagePriority.HIGH,
            filter = KmeMessageFilter.BREAKOUT
        )
    }

    /**
     * Subscribing for the room events related to content sharing
     */
    override fun subscribe(listener: IKmeContentModule.KmeContentListener) {
        this.listener = listener
        savedMessage?.payload?.let {
            setActiveContent(it)
            savedMessage = null
        }
    }

    private val activeContentHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.INIT_ACTIVE_CONTENT,
                KmeMessageEvent.SET_ACTIVE_CONTENT -> {
                    val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? =
                        message.toType()
                    if (listener == null) {
                        savedMessage = contentMessage
                    } else {
                        contentMessage?.payload?.let {
                            setActiveContent(it)
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * Un-subscribing from the room events related to content sharing
     */
    override fun unsubscribe() {
        roomController.removeListener(activeContentHandler)
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

    /**
     * Mute/Un-mute presented audio
     */
    override fun muteActiveContent(isMute: Boolean) {
        isMuted = isMute
        (contentView as? KmeMediaContentFragment)?.mute(isMute)
    }

    /**
     * Destroy active content
     */
    override fun destroy() {
        contentView = null
        listener?.onContentNotAvailable(null)
    }

    /**
     * Setting actual shared content
     */
    private fun setActiveContent(payload: SetActiveContentPayload) {
        payload.contentType?.let { type ->
            when (type) {
                VIDEO, AUDIO, YOUTUBE, KALTURA -> {
                    contentView = KmeMediaContentFragment.newInstance(payload)
                    (contentView as? KmeMediaContentFragment)?.mute(isMuted)
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
            contentView?.let { view ->
                listener?.onContentAvailable(view)
            } ?: run {
                listener?.onContentNotAvailable(type)
            }
        }
    }

}
