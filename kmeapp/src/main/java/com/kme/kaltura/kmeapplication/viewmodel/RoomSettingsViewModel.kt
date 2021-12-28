package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.util.extensions.ifNonNull
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.SetParticipantModerator
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.RoomSettingsChangedPayload
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

class RoomSettingsViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val toggleAllMicsByAdmin = MutableLiveData<Boolean>()
    val toggleAllMicsByAdminLiveData get() = toggleAllMicsByAdmin as LiveData<Boolean>

    private val toggleAllCamsByAdmin = MutableLiveData<Boolean>()
    val toggleAllCamsByAdminLiveData get() = toggleAllCamsByAdmin as LiveData<Boolean>

    private val chatSettingsChanged =
        MutableLiveData<Pair<KmePermissionKey?, KmePermissionValue?>>()
    val chatSettingsChangedLiveData
        get() = chatSettingsChanged as LiveData<Pair<KmePermissionKey?, KmePermissionValue?>>

    private val youModerator = MutableLiveData<Boolean>()
    val youModeratorLiveData get() = youModerator as LiveData<Boolean>

    private val moderator = MutableLiveData<Pair<Long, Boolean>>()
    val moderatorLiveData get() = moderator as LiveData<Pair<Long, Boolean>>

    fun subscribe() {
        kmeSdk.roomController.listen(
            roomSettingsHandler,
            KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED,
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR,
            KmeMessageEvent.ROOM_SETTINGS_CHANGED
        )
    }

    private val roomSettingsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED -> {
                    val settingsMessage: KmeRoomSettingsModuleMessage<KmeRoomSettingsModuleMessage.RoomDefaultSettingsChangedPayload>? =
                        message.toType()
                    val settingsPayload = settingsMessage?.payload

                    when (settingsPayload?.moduleName) {
                        KmePermissionModule.CHAT_MODULE -> {
                            val chatSettingsMessage: KmeRoomSettingsModuleMessage<KmeRoomSettingsModuleMessage.RoomChatSettingsChangedPayload>? =
                                message.toType()
                            val chatSettingsPayload = chatSettingsMessage?.payload

                            val pair = Pair(
                                chatSettingsPayload?.permissionsKey,
                                chatSettingsPayload?.permissionsValue
                            )
                            chatSettingsChanged.value = pair
                        }
                        else -> {
                        }
                    }
                }
                KmeMessageEvent.SET_PARTICIPANT_MODERATOR -> {
                    val settingsMessage: KmeParticipantsModuleMessage<SetParticipantModerator>? =
                        message.toType()

                    ifNonNull(
                        settingsMessage?.payload?.targetUserId,
                        settingsMessage?.payload?.isModerator
                    ) { userId, isModerator ->
                        moderator.value = Pair(userId, isModerator)

                        val youId = kmeSdk.userController.getCurrentUserInfo()?.getUserId() ?: 0
                        if (youId == userId) {
                            youModerator.value = isModerator
                        }
                    }
                }
                KmeMessageEvent.ROOM_SETTINGS_CHANGED -> {
                    val settingsMessage: KmeRoomSettingsModuleMessage<RoomSettingsChangedPayload>? =
                        message.toType()

                    when (settingsMessage?.payload?.changedRoomSetting) {
                        KmePermissionKey.MUTE_ALL_MICS -> {
                            toggleAllMicsByAdmin.value =
                                settingsMessage.payload?.roomSettingValue == KmePermissionValue.ON
                        }
                        KmePermissionKey.MUTE_ALL_CAMS -> {
                            toggleAllCamsByAdmin.value =
                                settingsMessage.payload?.roomSettingValue == KmePermissionValue.ON
                        }
                        else -> {
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

}
