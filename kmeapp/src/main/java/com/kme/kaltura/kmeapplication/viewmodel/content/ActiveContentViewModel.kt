package com.kme.kaltura.kmeapplication.viewmodel.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeSlidesPlayerModuleMessage.SlideChangedPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class ActiveContentViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val setActiveContent = MutableLiveData<SetActiveContentPayload>()
    val setActiveContentLiveData get() = setActiveContent as LiveData<SetActiveContentPayload>

    private val slideChanged = MutableLiveData<Int>()
    val slideChangedLiveData get() = slideChanged as LiveData<Int>

    fun subscribe() {
        kmeSdk.roomController.listen(
            activeContentHandler,
            KmeMessageEvent.INIT_ACTIVE_CONTENT,
            KmeMessageEvent.SET_ACTIVE_CONTENT
        )

        kmeSdk.roomController.listen(
            slidePlayerHandler,
            KmeMessageEvent.SLIDE_CHANGED
        )
    }

    fun getCookie(): String = kmeSdk.getCookies().toNonNull()

    fun getFilesUrl(): String = kmeSdk.getFilesUrl().toNonNull()

    fun userType(): KmeUserType? = kmeSdk.userController.getCurrentParticipant()?.userType

    fun enabledControls(): Boolean = kmeSdk.userController.getCurrentParticipant()
        ?.userPermissions?.playlistModule?.defaultSettings?.isModerator == KmePermissionValue.ON

    private val activeContentHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.INIT_ACTIVE_CONTENT -> {
                    val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        setActiveContent.postValue(it)
                    }
                }
                KmeMessageEvent.SET_ACTIVE_CONTENT -> {
                    val contentMessage: KmeActiveContentModuleMessage<SetActiveContentPayload>? =
                        message.toType()

                    contentMessage?.payload?.let {
                        setActiveContent.postValue(it)
                    }
                }
            }
        }
    }

    private val slidePlayerHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.SLIDE_CHANGED -> {
                    val msg: KmeSlidesPlayerModuleMessage<SlideChangedPayload>? = message.toType()
                    msg?.payload?.let {
                        slideChanged.postValue(it.nextActiveSlide)
                    }
                }
            }
        }
    }

    fun getKalturaPartnerId(): Int {
        val partnerId = kmeSdk.roomController.roomSettings?.roomInfo?.integrations?.kaltura?.company?.id
        return partnerId?.toInt() ?: 0
    }

}
