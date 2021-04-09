package com.kme.kaltura.kmesdk.content.whiteboard

import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.util.livedata.ConsumableValue
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import okhttp3.internal.toImmutableList

class KmeWhiteboardContentViewModel(
    private val roomController: IKmeRoomController
) : ViewModel() {

    private val setActivePage = MutableLiveData<String>()
    val setActivePageLiveData get() = setActivePage as LiveData<String>

    private val whiteboardPageData =
        MutableLiveData<ConsumableValue<List<WhiteboardPayload.Drawing>>>()
    val whiteboardPageLiveData get() = whiteboardPageData as LiveData<ConsumableValue<List<WhiteboardPayload.Drawing>>>

    private val whiteboardCleared = MutableLiveData<ConsumableValue<Nothing?>>()
    val whiteboardClearedLiveData get() = whiteboardCleared as LiveData<ConsumableValue<Nothing?>>

    private val backgroundChanged = MutableLiveData<ConsumableValue<KmeWhiteboardBackgroundType?>>()
    val backgroundChangedLiveData get() = backgroundChanged as LiveData<ConsumableValue<KmeWhiteboardBackgroundType?>>

    private val receiveDrawing = MutableLiveData<ConsumableValue<WhiteboardPayload.Drawing>>()
    val receiveDrawingLiveData get() = receiveDrawing as LiveData<ConsumableValue<WhiteboardPayload.Drawing>>

    private val receiveLaserPosition = MutableLiveData<ConsumableValue<PointF>>()
    val receiveLaserPositionLiveData get() = receiveLaserPosition as LiveData<ConsumableValue<PointF>>

    private val hideLaser = MutableLiveData<ConsumableValue<Nothing?>>()
    val hideLaserLiveData get() = hideLaser as LiveData<ConsumableValue<Nothing?>>

    private val deleteDrawing = MutableLiveData<ConsumableValue<String>>()
    val deleteDrawingLiveData get() = deleteDrawing as LiveData<ConsumableValue<String>>

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
            KmeMessageEvent.DELETE_DRAWING
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
                        whiteboardPageData.postValue(ConsumableValue(it))
                        savedDrawings.clear()
                        savedDrawings.addAll(it)
                    }
                }
                KmeMessageEvent.RECEIVE_LASER_POSITION -> {
                    val contentMessage: KmeWhiteboardModuleMessage<ReceivedLaserPositionPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        laserPosition.set(it.laserX, it.laserY)
                        receiveLaserPosition.postValue(ConsumableValue(laserPosition))
                    }
                }
                KmeMessageEvent.LASER_DEACTIVATED -> {
                    hideLaser.postValue(ConsumableValue(null))
                }
                KmeMessageEvent.RECEIVE_DRAWING, KmeMessageEvent.RECEIVE_TRANSFORMATION -> {
                    val contentMessage: KmeWhiteboardModuleMessage<ReceiveDrawingPayload>? =
                        message.toType()

                    contentMessage?.payload?.drawing?.let {
                        receiveDrawing.postValue(ConsumableValue(it))
                        savedDrawings.add(it)
                    }
                }
                KmeMessageEvent.DELETE_DRAWING -> {
                    val contentMessage: KmeWhiteboardModuleMessage<DeleteDrawingPayload>? =
                        message.toType()

                    contentMessage?.payload?.layer?.let {
                        deleteDrawing.postValue(ConsumableValue(it))
                        savedDrawings.removeAll { drawing -> drawing.layer == it }
                    }
                }
                KmeMessageEvent.WHITEBOARD_PAGE_CLEARED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (boardId == it.boardId && pageId == it.pageId) {
                            whiteboardCleared.postValue(ConsumableValue(null))
                            savedDrawings.clear()
                        }
                    }
                }
                KmeMessageEvent.WHITEBOARD_ALL_PAGES_CLEARED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (boardId == it.boardId) {
                            whiteboardCleared.postValue(ConsumableValue(null))
                            savedDrawings.clear()
                        }
                    }
                }
                KmeMessageEvent.WHITEBOARD_BACKGROUND_TYPE_CHANGED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<BackgroundTypeChangedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (pageId == it.pageId) {
                            backgroundChanged.postValue(ConsumableValue(it.backgroundType))
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

}
