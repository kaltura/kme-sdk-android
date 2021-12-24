package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinViewModel
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.livedata.LiveEvent
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import okhttp3.internal.toImmutableList

class KmeWhiteboardContentViewModel : ViewModel(), KmeKoinViewModel {

    private val roomController:  IKmeRoomController by scopedInject()

    private val setActivePage = MutableLiveData<String>()
    val setActivePageLiveData get() = setActivePage as LiveData<String>

    private val whiteboardPageData = LiveEvent<List<WhiteboardPayload.Drawing>>()
    val whiteboardPageLiveData get() = whiteboardPageData

    private val whiteboardCleared = LiveEvent<Unit>()
    val whiteboardClearedLiveData get() = whiteboardCleared

    private val backgroundChanged = LiveEvent<KmeWhiteboardBackgroundType?>()
    val backgroundChangedLiveData get() = backgroundChanged

    private val receiveDrawing = LiveEvent<WhiteboardPayload.Drawing>()
    val receiveDrawingLiveData get() = receiveDrawing

    private val receiveLaserPosition = LiveEvent<PointF>()
    val receiveLaserPositionLiveData get() = receiveLaserPosition

    private val hideLaser = LiveEvent<Unit>()
    val hideLaserLiveData get() = hideLaser

    private val deleteDrawing = LiveEvent<String>()
    val deleteDrawingLiveData get() = deleteDrawing

    private val savedDrawings: MutableList<WhiteboardPayload.Drawing> = mutableListOf()
    val savedDrawingsList get() = savedDrawings.toImmutableList()

    var boardId: String? = null
        private set

    var pageId: String? = null
        private set

    private val laserPosition by lazy { PointF(0f, 0f) }

    fun subscribe() {
        roomController.listen(
            whiteboardHandler,
            KmeMessageEvent.WHITEBOARD_PAGE_DATA,
            KmeMessageEvent.WHITEBOARD_PAGE_CLEARED,
            KmeMessageEvent.WHITEBOARD_ALL_PAGES_CLEARED,
            KmeMessageEvent.RECEIVE_LASER_POSITION,
            KmeMessageEvent.LASER_DEACTIVATED,
            KmeMessageEvent.RECEIVE_DRAWING,
            KmeMessageEvent.RECEIVE_TRANSFORMATION,
            KmeMessageEvent.WHITEBOARD_SET_ACTIVE_PAGE,
            KmeMessageEvent.WHITEBOARD_PAGE_CREATED,
            KmeMessageEvent.WHITEBOARD_BACKGROUND_TYPE_CHANGED,
            KmeMessageEvent.DELETE_DRAWING,
            priority = KmeMessagePriority.NORMAL
        )
    }

    private val whiteboardHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.WHITEBOARD_PAGE_DATA -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageDataPayload>? =
                        message.toType()

                    boardId = contentMessage?.payload?.boardId
                    pageId = contentMessage?.payload?.pageId

                    contentMessage?.payload?.drawings?.let {
                        whiteboardPageData.postValue(it)
                        savedDrawings.clear()
                        savedDrawings.addAll(it)
                    }
                }
                KmeMessageEvent.RECEIVE_LASER_POSITION -> {
                    val contentMessage: KmeWhiteboardModuleMessage<ReceivedLaserPositionPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        laserPosition.set(it.laserX, it.laserY)
                        receiveLaserPosition.postValue(laserPosition)
                    }
                }
                KmeMessageEvent.LASER_DEACTIVATED -> {
                    hideLaser.postValue(Unit)
                }
                KmeMessageEvent.RECEIVE_DRAWING, KmeMessageEvent.RECEIVE_TRANSFORMATION -> {
                    val contentMessage: KmeWhiteboardModuleMessage<ReceiveDrawingPayload>? =
                        message.toType()

                    contentMessage?.payload?.drawing?.let {
                        receiveDrawing.postValue(it)
                        savedDrawings.add(it)
                    }
                }
                KmeMessageEvent.DELETE_DRAWING -> {
                    val contentMessage: KmeWhiteboardModuleMessage<DeleteDrawingPayload>? =
                        message.toType()

                    contentMessage?.payload?.layer?.let {
                        deleteDrawing.postValue(it)
                        savedDrawings.removeAll { drawing -> drawing.layer == it }
                    }
                }
                KmeMessageEvent.WHITEBOARD_PAGE_CLEARED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (boardId == it.boardId && pageId == it.pageId) {
                            whiteboardCleared.postValue(Unit)
                            savedDrawings.clear()
                        }
                    }
                }
                KmeMessageEvent.WHITEBOARD_ALL_PAGES_CLEARED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (boardId == it.boardId) {
                            whiteboardCleared.postValue(Unit)
                            savedDrawings.clear()
                        }
                    }
                }
                KmeMessageEvent.WHITEBOARD_BACKGROUND_TYPE_CHANGED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<BackgroundTypeChangedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (pageId == it.pageId) {
                            backgroundChanged.postValue(it.backgroundType)
                        }
                    }
                }
                KmeMessageEvent.WHITEBOARD_SET_ACTIVE_PAGE -> {
                    val contentMessage: KmeWhiteboardModuleMessage<SetActivePagePayload>? =
                        message.toType()

                    contentMessage?.payload?.activePageId?.let {
                        pageId = it
                        setActivePage.postValue(pageId)
                    }
                }
                KmeMessageEvent.WHITEBOARD_PAGE_CREATED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<PageCreatedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        pageId = it.id
                        setActivePage.postValue(pageId)
                        savedDrawings.clear()
                        whiteboardPageData.postValue(savedDrawings)
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        roomController.removeListener(whiteboardHandler)
    }

    override fun onClosed() {
        onCleared()
    }

}
