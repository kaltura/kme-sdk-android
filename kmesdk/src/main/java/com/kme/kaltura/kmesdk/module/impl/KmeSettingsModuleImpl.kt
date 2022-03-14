package com.kme.kaltura.kmesdk.module.impl

import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalSettingsModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.ifNonNull
import com.kme.kaltura.kmesdk.module.IKmeSettingsModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeChatModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeDefaultSettings
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeParticipantsModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserSetting
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.SetParticipantModerator
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmeModuleVisibilityValue
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject

/**
 * An implementation for room settings handling
 */
internal class KmeSettingsModuleImpl : KmeController(), IKmeInternalSettingsModule {

    private val messageManager: KmeMessageManager by inject()
    private val userController: IKmeUserController by inject()
    private val roomController: IKmeRoomController by scopedInject()

    private var listeners: MutableSet<IKmeSettingsModule.KmeSettingsListener> = mutableSetOf()

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    override fun subscribe() {
        messageManager.listen(
            roomSettingsHandler,
            KmeMessageEvent.ROOM_STATE,
            KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED,
            KmeMessageEvent.CHANGE_PARTICIPANT_PERMISSIONS,
            KmeMessageEvent.ROOM_SETTINGS_CHANGED,
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR,
            priority = KmeMessagePriority.HIGH
        )
    }

    /**
     * Setup setting listener
     */
    override fun subscribe(listener: IKmeSettingsModule.KmeSettingsListener) {
        listeners.add(listener)
    }

    /**
     * UpdateSettings for the room events related to change settings
     * for the users and for the room itself
     */
    override fun updateSettings(roomSetting: KmeSettingsV2?, userSetting: KmeUserSetting) {
        for (listener in listeners) {
            listener.onSettingsChanged(roomSetting, userSetting)
        }
    }

    /**
     * Listen for subscribed events
     */
    private val roomSettingsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_STATE -> {
                    for (listener in listeners) {
                        listener.onModeratorStateChanged(userController.isModerator())
                    }
                }
                KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED -> {
                    val settingsMessage: KmeRoomSettingsModuleMessage<RoomDefaultSettingsChangedPayload>? =
                        message.toType()

                    val settingsPayload = settingsMessage?.payload
                    when (settingsPayload?.moduleName) {
                        KmePermissionModule.CHAT_MODULE -> {
                            val chatSettingsMessage: KmeRoomSettingsModuleMessage<RoomChatSettingsChangedPayload>? =
                                message.toType()
                            val chatSettingsPayload = chatSettingsMessage?.payload
                            handleChatSetting(
                                chatSettingsPayload?.permissionsKey,
                                chatSettingsPayload?.permissionsValue
                            )
                            updateSettings(userController.getCurrentParticipant()?.userPermissions, userController.getCurrentUserSetting())
                        }
                        KmePermissionModule.PARTICIPANTS_MODULE -> {
                            val participantSettingsMessage: KmeRoomSettingsModuleMessage<RoomParticipantSettingsChangedPayload>? =
                                message.toType()
                            val participantSettingsPayload = participantSettingsMessage?.payload
                            handleParticipantSetting(
                                participantSettingsPayload?.permissionsKey,
                                participantSettingsPayload?.permissionsValue
                            )
                            updateSettings(userController.getCurrentParticipant()?.userPermissions, userController.getCurrentUserSetting())
                        }
                        else -> {
                        }
                    }
                }
                KmeMessageEvent.CHANGE_PARTICIPANT_PERMISSIONS -> {
                    val settingsMessage: KmeParticipantSettingsModuleMessage<KmeParticipantSettingsModuleMessage.ParticipantSettingsChangedPayload>? =
                        message.toType()
                    val payload = settingsMessage?.payload

                    when (payload?.changedPermissionsModule){
                        KmePermissionModule.PARTICIPANTS_MODULE -> {
                            userController.getCurrentUserSetting().apply {
                                participants = payload.changedPermissionValue
                            }
                            updateSettings(userController.getCurrentParticipant()?.userPermissions, userController.getCurrentUserSetting())
                        }
                        KmePermissionModule.CHAT_MODULE -> {
                            userController.getCurrentUserSetting().apply {
                                when(payload.changedPermissionKey){
                                    KmePermissionKey.QNA_CHAT -> {
                                        qnaChat = payload.changedPermissionValue
                                    }
                                    KmePermissionKey.PUBLIC_CHAT -> {
                                        publicChat = payload.changedPermissionValue
                                    }
                                }
                            }
                            updateSettings(userController.getCurrentParticipant()?.userPermissions, userController.getCurrentUserSetting())
                        }
                    }
                }
                KmeMessageEvent.ROOM_SETTINGS_CHANGED -> {
                    val settingsMessage: KmeRoomSettingsModuleMessage<RoomSettingsChangedPayload>? =
                        message.toType()
                    ifNonNull(
                        settingsMessage?.payload?.changedRoomSetting,
                        settingsMessage?.payload?.roomSettingValue
                    ) { key, value ->
                        when (key) {
                            KmePermissionKey.CLASS_MODE -> {
                                roomController.webRTCServer?.roomInfo?.settingsV2?.general?.classMode =
                                    value
                            }
                            KmePermissionKey.MUTE_ON_PLAY -> {
                                roomController.webRTCServer?.roomInfo?.settingsV2?.general?.muteOnPlay =
                                    value
                            }
                            KmePermissionKey.MUTE_MODE -> {
                                roomController.webRTCServer?.roomInfo?.settingsV2?.general?.muteMode =
                                    value
                            }
                            KmePermissionKey.MUTE_ALL_MICS -> {
                                roomController.webRTCServer?.roomInfo?.settingsV2?.general?.muteAllMics =
                                    value
                            }
                            KmePermissionKey.MUTE_ALL_CAMS -> {
                                roomController.webRTCServer?.roomInfo?.settingsV2?.general?.muteAllCams =
                                    value
                            }
                            else -> {
                            }
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
                        handleModeratorSetting(userId, isModerator)
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * Update application listeners in case some of settings were changed
     */
    private fun handleChatSetting(
        key: KmePermissionKey?,
        value: KmePermissionValue?
    ) {
        val currentParticipant = userController.getCurrentParticipant()
        if (currentParticipant != null) {
            val userPermissions = currentParticipant.userPermissions ?: KmeSettingsV2()
            val chatModule = userPermissions.chatModule ?: KmeChatModule()
            val chatSettings = chatModule.defaultSettings ?: KmeDefaultSettings()

            when (key) {
                KmePermissionKey.QNA_CHAT -> {
                    chatSettings.qnaChat = value
                }
                KmePermissionKey.PUBLIC_CHAT -> {
                    chatSettings.publicChat = value
                }
                KmePermissionKey.START_PRIVATE_CHAT -> {
                    chatSettings.startPrivateChat = value
                }
                else -> {
                }
            }

            chatModule.defaultSettings = chatSettings
            userPermissions.chatModule = chatModule
            currentParticipant.userPermissions = userPermissions
        }
    }

    private fun handleParticipantSetting(
        key: KmePermissionKey?,
        value: KmeModuleVisibilityValue?
    ) {
        val currentParticipant = userController.getCurrentParticipant()
        if (currentParticipant != null) {
            val userPermissions = currentParticipant.userPermissions ?: KmeSettingsV2()
            val participantModule = userPermissions.participantsModule ?: KmeParticipantsModule()
            when (key) {
                KmePermissionKey.VISIBILITY -> {
                    participantModule.visibility = value
                }
            }

            userPermissions.participantsModule = participantModule
            currentParticipant.userPermissions = userPermissions
        }
    }

    /**
     * Update application listeners in case room moderator was changed by admin
     */
    private fun handleModeratorSetting(
        userId: Long,
        isModerator: Boolean,
    ) {
        val currentParticipant = userController.getCurrentParticipant()
        if (currentParticipant != null && userId == currentParticipant.userId) {
            currentParticipant.isModerator = isModerator
            for (listener in listeners) {
                listener.onModeratorStateChanged(isModerator)
            }
        }
    }

}