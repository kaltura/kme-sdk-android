package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeSettingsModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeChatModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeDefaultSettings
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeSettingsV2
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.SetParticipantModerator
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject

/**
 * An implementation for room settings handling
 */
internal class KmeSettingsModuleImpl : KmeController(), IKmeSettingsModule {

    private val messageManager: KmeMessageManager by inject()
    private val userController: IKmeUserController by inject()

    /**
     * Subscribing for the room events related to change settings
     * for the users and for the room itself
     */
    override fun subscribe() {
        messageManager.listen(
            roomSettingsHandler,
            KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED,
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR
        )
    }

    /**
     * Listen for subscribed events
     */
    private val roomSettingsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_DEFAULT_SETTINGS_CHANGED -> {
                    val settingsMessage: KmeRoomSettingsModuleMessage<KmeRoomSettingsModuleMessage.RoomDefaultSettingsChangedPayload>? =
                        message.toType()
                    val settingsPayload = settingsMessage?.payload

                    val currentParticipant = when (settingsPayload?.moduleName) {
                        KmePermissionModule.CHAT_MODULE -> {
                            handleChatSetting(
                                settingsPayload.permissionsKey,
                                settingsPayload.permissionsValue
                            )
                        }
                        else -> null
                    }

                    if (currentParticipant != null) {
                        userController.updateParticipant(currentParticipant)
                    }
                }
                KmeMessageEvent.SET_PARTICIPANT_MODERATOR -> {
                    val settingsMessage: KmeParticipantsModuleMessage<SetParticipantModerator>? =
                        message.toType()

                    val userId = settingsMessage?.payload?.targetUserId
                    val isModerator = settingsMessage?.payload?.isModerator
                    if (userId != null && isModerator != null) {
                        val currentParticipant = handleModeratorSetting(userId, isModerator)
                        if (currentParticipant != null) {
                            userController.updateParticipant(currentParticipant)
                        }
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
    ): KmeParticipant? {
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

        return currentParticipant
    }

    /**
     * Update application listeners in case room moderator was changed by admin
     */
    private fun handleModeratorSetting(
        userId: Long,
        isModerator: Boolean
    ): KmeParticipant? {
        val currentParticipant = userController.getCurrentParticipant()
        if (currentParticipant != null && userId == currentParticipant.userId) {
            currentParticipant.isModerator = isModerator
            return currentParticipant
        }
        return null
    }

}