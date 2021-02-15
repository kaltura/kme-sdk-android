package com.kme.kaltura.kmeapplication.viewmodel.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType

class WhiteboardContentViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val whiteboardPageData =
        MutableLiveData<List<WhiteboardPayload.Drawing>>()
    val whiteboardPageLiveData
        get() = whiteboardPageData as LiveData<List<WhiteboardPayload.Drawing>>

    private val whiteboardCleared =
        MutableLiveData<Nothing>()
    val whiteboardClearedLiveData
        get() = whiteboardCleared as LiveData<Nothing>

    private val backgroundChanged =
        MutableLiveData<KmeWhiteboardBackgroundType>()
    val backgroundChangedLiveData
        get() = backgroundChanged as LiveData<KmeWhiteboardBackgroundType>

    private val receiveDrawing =
        MutableLiveData<WhiteboardPayload.Drawing>()
    val receiveDrawingLiveData
        get() = receiveDrawing as LiveData<WhiteboardPayload.Drawing>

    private val deleteDrawing =
        MutableLiveData<String>()
    val deleteDrawingLiveData
        get() = deleteDrawing as LiveData<String>

    private val setActivePage =
        MutableLiveData<String>()
    val setActivePageLiveData
        get() = setActivePage as LiveData<String>

    var boardId: String? = null
        private set

    var pageId: String? = null
        private set

    fun subscribe() {
        kmeSdk.roomController.listen(
            whiteboardHandler,
            KmeMessageEvent.WHITEBOARD_PAGE_DATA,
            KmeMessageEvent.WHITEBOARD_PAGE_CLEARED,
            KmeMessageEvent.WHITEBOARD_ALL_PAGES_CLEARED,
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
                        whiteboardPageData.postValue(it)
                    }
                }
                KmeMessageEvent.RECEIVE_DRAWING, KmeMessageEvent.RECEIVE_TRANSFORMATION -> {
                    val contentMessage: KmeWhiteboardModuleMessage<ReceiveDrawingPayload>? =
                        message.toType()

                    contentMessage?.payload?.drawing?.let {
                        receiveDrawing.postValue(it)
                    }
                }
                KmeMessageEvent.DELETE_DRAWING -> {
                    val contentMessage: KmeWhiteboardModuleMessage<DeleteDrawingPayload>? =
                        message.toType()

                    contentMessage?.payload?.layer?.let {
                        deleteDrawing.postValue(it)
                    }
                }
                KmeMessageEvent.WHITEBOARD_PAGE_CLEARED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (boardId == it.boardId && pageId == it.pageId) {
                            whiteboardCleared.postValue(null)
                        }
                    }
                }
                KmeMessageEvent.WHITEBOARD_ALL_PAGES_CLEARED -> {
                    val contentMessage: KmeWhiteboardModuleMessage<WhiteboardPageClearedPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        if (boardId == it.boardId) {
                            whiteboardCleared.postValue(null)
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
                    }
                }
            }
        }
    }

}
