package com.kme.kaltura.kmesdk.controller.impl.internal

import com.kme.kaltura.kmesdk.controller.IKmeRoomSettingsController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeChatModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeDefaultSettings
import com.kme.kaltura.kmesdk.toType
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.SetParticipantModerator
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomSettingsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.permission.KmeUserPermissions
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import org.koin.core.inject

internal class KmeRoomSettingsControllerImpl : KmeController(), IKmeRoomSettingsController {

    private val messageManager: KmeMessageManager by inject()
    private val userController: IKmeUserController by inject()

    override fun subscribe() {
        messageManager.listen(
            roomSettingsHandler,
            KmeMessageEvent.ROOM_SETTINGS_CHANGED,
            KmeMessageEvent.SET_PARTICIPANT_MODERATOR
        )
    }

    private val roomSettingsHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.ROOM_SETTINGS_CHANGED -> {
                    val settingsMessage: KmeRoomSettingsModuleMessage<KmeRoomSettingsModuleMessage.RoomSettingsChangedPayload>? =
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
                        userController.currentParticipant = currentParticipant
                    }
                }
                KmeMessageEvent.SET_PARTICIPANT_MODERATOR -> {
                    val settingsMessage: KmeParticipantsModuleMessage<SetParticipantModerator>? =
                        message.toType()
                    val currentParticipant = handleModeratorSetting(settingsMessage)
                    if (currentParticipant != null) {
                        userController.currentParticipant = currentParticipant
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun handleChatSetting(
        key: KmePermissionKey?,
        value: KmePermissionValue?
    ): KmeParticipant? {
        val currentParticipant = userController.currentParticipant
        if (currentParticipant != null) {
            val userPermissions = currentParticipant.userPermissions ?: KmeUserPermissions()
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

    private fun handleModeratorSetting(
        settingsMessage: KmeParticipantsModuleMessage<SetParticipantModerator>?
    ): KmeParticipant? {
        val currentParticipant = userController.currentParticipant
        if (currentParticipant != null) {
            currentParticipant.isModerator = settingsMessage?.payload?.isModerator
        }

        return currentParticipant
    }

}