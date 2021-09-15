package com.kme.kaltura.kmesdk.content.slides

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.di.inject
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.toNonNull
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage.AnnotationStateChangedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage.SlideChangedPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import org.koin.core.inject

class KmeSlidesContentViewModel : ViewModel(), KmeKoinComponent {

    private val userController: IKmeUserController by inject()
    private val metadataController: IKmeMetadataController by inject()
    private val roomController: IKmeRoomController by controllersScope().inject()
    private val prefs: IKmePreferences by inject()

    private val slideChanged = MutableLiveData<Int>()
    val slideChangedLiveData get() = slideChanged as LiveData<Int>

    private val annotationStateChanged = MutableLiveData<Boolean>()
    val annotationStateChangedLiveData get() = annotationStateChanged as LiveData<Boolean>

    private val youModerator = MutableLiveData<Boolean>()
    val youModeratorLiveData get() = youModerator as LiveData<Boolean>

    fun subscribe() {
        roomController.listen(
            slidePlayerHandler,
            KmeMessageEvent.SLIDE_CHANGED,
            KmeMessageEvent.ANNOTATIONS_STATE_CHANGED,
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR
        )
    }

    fun isAnnotationsEnabled(): Boolean = roomController.roomMetadata?.annotationsEnabled ?: true

    fun getCookie(): String = prefs.getString(KmePrefsKeys.COOKIE).toNonNull()

    fun getFilesUrl(): String = metadataController.getMetadata()?.filesUrl.toNonNull()

    fun userType(): KmeUserType? = userController.getCurrentParticipant()?.userType

    private val slidePlayerHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SLIDE_CHANGED -> {
                    val msg: KmeSlidesPlayerModuleMessage<SlideChangedPayload>? = message.toType()

                    msg?.payload?.let {
                        slideChanged.postValue(it.nextActiveSlide)
                    }
                }
                KmeMessageEvent.ANNOTATIONS_STATE_CHANGED -> {
                    val msg: KmeSlidesPlayerModuleMessage<AnnotationStateChangedPayload>? =
                        message.toType()

                    msg?.payload?.let {
                        annotationStateChanged.postValue(it.annotationsEnabled)
                    }
                }
                KmeMessageEvent.SET_PARTICIPANT_MODERATOR -> {
                    val settingsMessage: KmeParticipantsModuleMessage<KmeParticipantsModuleMessage.SetParticipantModerator>? =
                        message.toType()

                    val userId = settingsMessage?.payload?.targetUserId
                    val isModerator = settingsMessage?.payload?.isModerator
                    val youId = userController.getCurrentUserInfo()?.getUserId() ?: 0
                    if (youId == userId) {
                        youModerator.value = isModerator
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        roomController.removeListener(slidePlayerHandler)
    }

}
