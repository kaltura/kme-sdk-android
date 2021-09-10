package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.controller.room.IKmePeerConnectionModule
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioDevice.EARPIECE
import com.kme.kaltura.kmesdk.webrtc.audio.KmeAudioDevice.SPEAKER_PHONE
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.RoomStatePayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.StartedPublishPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState

class PeerConnectionViewModel(
    private val kmeSdk: KME
) : ViewModel(), IKmePeerConnectionModule.KmePeerConnectionEvents {

    private val viewerAdd = MutableLiveData<Long>()
    val viewerAddLiveData get() = viewerAdd as LiveData<Long>

    private val publisherAdd = MutableLiveData<Long>()
    val publisherAddLiveData get() = publisherAdd as LiveData<Long>

    private val publisherAdded = MutableLiveData<Boolean>()
    val publisherAddedLiveData get() = publisherAdded as LiveData<Boolean>

    private val peerConnectionRemove = MutableLiveData<Long>()
    val participantRemoveLiveData get() = peerConnectionRemove as LiveData<Long>

    private val userSpeaking = MutableLiveData<Pair<Long, Boolean>>()
    val userSpeakingLiveData get() = userSpeaking as LiveData<Pair<Long, Boolean>>

    private val speakerEnabled = MutableLiveData<Boolean>()
    val speakerEnabledLiveData get() = speakerEnabled as LiveData<Boolean>

    private val liveEnabled = MutableLiveData<Boolean>()
    val liveEnabledLiveData get() = liveEnabled as LiveData<Boolean>

    private val micEnabled = MutableLiveData<Boolean>()
    val micEnabledLiveData get() = micEnabled as LiveData<Boolean>

    private val camEnabled = MutableLiveData<Boolean>()
    val camEnabledLiveData get() = camEnabled as LiveData<Boolean>

    private val frontCamEnabled = MutableLiveData<Boolean>()
    val frontCameraEnabledLiveData get() = frontCamEnabled as LiveData<Boolean>

    private val publisherId: Long by lazy {
        kmeSdk.userController.getCurrentUserInfo()?.getUserId() ?: 0
    }

    private var companyId: Long = 0
    private var roomId: Long = 0

    fun setRoomData(companyId: Long, roomId: Long) {
        this.companyId = companyId
        this.roomId = roomId

        kmeSdk.roomController.audioModule.start()
        kmeSdk.roomController.peerConnectionModule.initialize(
            roomId,
            companyId,
            this
        )
        subscribeForEvents()
    }

    fun setRoomState(payload: RoomStatePayload?) {
        payload?.participants?.forEach {
            if (it.value.liveMediaState == KmeMediaDeviceState.LIVE_SUCCESS) {
                if (it.key != publisherId.toString()) {
                    viewerAdd.value = it.key.toLongOrNull()
                }
            }
        }
    }

    private fun subscribeForEvents() {
        kmeSdk.roomController.listen(
            peerConnectionHandler,
            KmeMessageEvent.USER_STARTED_TO_PUBLISH,
            KmeMessageEvent.USER_MEDIA_STATE_CHANGED
        )
    }

    fun isPublishing() = kmeSdk.roomController.peerConnectionModule.isPublishing()

    private fun startPublish(localRenderer: KmeSurfaceRendererView) {
//        kmeSdk.roomController.peerConnectionModule.addPublisher(
//            publisherId.toString(),
//            localRenderer,
//            micEnabled.value ?: true,
//            camEnabled.value ?: true,
//            frontCamEnabled.value ?: true
//        )

        publisherAdded.value = true
    }

    fun startPublish(
        micEnabled: Boolean,
        camEnabled: Boolean,
        frontCamEnabled: Boolean
    ) {
        this.publisherAdded.value = false

        this.micEnabled.value = micEnabled
        this.camEnabled.value = camEnabled
        this.frontCamEnabled.value = frontCamEnabled

        createPublisherRenderer(publisherId)
    }

    fun toggleCamera() {
        camEnabled.value?.let { isCameraEnabled ->
            kmeSdk.roomController.peerConnectionModule.enableCamera(!isCameraEnabled)
        }
    }

    // Also called after publisher's actions
    fun toggleCamByAdmin(enable: Boolean, forAll: Boolean) {
        camEnabled.value?.let {
            if (it == enable) return

            val isAdmin = kmeSdk.userController.isAdminFor(companyId)
            val isModerator = kmeSdk.userController.isModerator()
            if ((isAdmin || isModerator) && forAll) {
                return
            }

            camEnabled.value = enable
            kmeSdk.roomController.peerConnectionModule.enableCamera(enable)
        }
    }

    fun toggleMic() {
        micEnabled.value?.let { isMicEnabled ->
            kmeSdk.roomController.peerConnectionModule.enableAudio(!isMicEnabled)
        }
    }

    // Also called after publisher's actions
    fun toggleMicByAdmin(enable: Boolean, forAll: Boolean) {
        micEnabled.value?.let {
            if (it == enable) return

            val isAdmin = kmeSdk.userController.isAdminFor(companyId)
            val isModerator = kmeSdk.userController.isModerator()
            if ((isAdmin || isModerator) && forAll) {
                return
            }

            micEnabled.value = enable
            kmeSdk.roomController.peerConnectionModule.enableAudio(enable)
        }
    }

    fun toggleLiveByAdmin(enable: Boolean) {
        liveEnabled.value?.let {
            if (it == enable) return

            if (enable) {
                createPublisherRenderer(publisherId)
            } else {
                kmeSdk.roomController.peerConnectionModule.disconnect(publisherId.toString())
            }
            liveEnabled.value = enable
        }
    }

    fun toggleSpeaker() {
        speakerEnabled.value?.let {
            speakerEnabled.value = !it
            if (!it) {
                kmeSdk.roomController.audioModule.setAudioDevice(SPEAKER_PHONE)
            } else {
                kmeSdk.roomController.audioModule.setAudioDevice(EARPIECE)
            }
        }
    }

    fun addPeerConnection(userId: Long, rendererView: KmeSurfaceRendererView) {
        if (publisherId == userId) {
            startPublish(rendererView)
        } else {
            if (speakerEnabled.value == null) {
                speakerEnabled.value = false
            }
            kmeSdk.roomController.peerConnectionModule.addViewer(userId.toString(), rendererView)
        }
    }

    fun removePeerConnection(id: String) {
        kmeSdk.roomController.peerConnectionModule.disconnect(id)
    }

    private fun createPublisherRenderer(userId: Long) {
        publisherAdd.value = userId
    }

    private val peerConnectionHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.USER_STARTED_TO_PUBLISH -> {
                    val msg: KmeStreamingModuleMessage<StartedPublishPayload>? = message.toType()
                    msg?.payload?.userId?.toLongOrNull()?.let {
                        viewerAdd.value = it
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onPublisherReady() {
        liveEnabled.postValue(true)
    }

    override fun onViewerReady(id: String) {

    }

    override fun onUserSpeaking(id: String, isSpeaking: Boolean) {
        userSpeaking.postValue(Pair(id.toLong(), isSpeaking))
    }

    override fun onPeerConnectionRemoved(id: String) {
        id.toLongOrNull()?.let {
            peerConnectionRemove.value = it
        }
    }

    override fun onPeerConnectionError(id: String, description: String) {

    }

    override fun onCleared() {
        super.onCleared()
        kmeSdk.roomController.audioModule.stop()
        kmeSdk.roomController.peerConnectionModule.disconnectAll()
    }

}
