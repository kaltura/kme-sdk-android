package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeParticipantModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.util.messages.buildAllHandsDownMessage
import com.kme.kaltura.kmesdk.util.messages.buildChangeMediaStateMessage
import com.kme.kaltura.kmesdk.util.messages.buildRaiseHandMessage
import com.kme.kaltura.kmesdk.util.messages.buildRemoveParticipantMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import org.koin.core.inject

/**
 * An implementation for desktop share actions
 */
class KmeParticipantModuleImpl : KmeController(), IKmeParticipantModule {

    private val roomController: IKmeRoomController by inject()

    /**
     * Raise participant hand
     */
    override fun raiseHand(
        roomId: Long,
        companyId: Long,
        userId: Long,
        targetUserId: Long,
        isRaise: Boolean
    ) {
        roomController.send(
            buildRaiseHandMessage(
                roomId,
                companyId,
                userId,
                targetUserId,
                isRaise
            )
        )
    }

    /**
     * Put all hands down in the room
     */
    override fun allHandsDown(roomId: Long, companyId: Long) {
        roomController.send(buildAllHandsDownMessage(roomId, companyId))
    }

    /**
     * Changes media state of participant in the room
     */
    override fun changeMediaState(
        roomId: Long,
        companyId: Long,
        userId: Long,
        mediaStateType: KmeMediaStateType,
        stateValue: KmeMediaDeviceState
    ) {
        roomController.send(
            buildChangeMediaStateMessage(
                roomId,
                companyId,
                userId,
                mediaStateType,
                stateValue
            )
        )
    }

    override fun remove(
        roomId: Long,
        companyId: Long,
        userId: Long,
        targetId: Long
    ) {
        roomController.send(
            buildRemoveParticipantMessage(
                roomId,
                companyId,
                userId,
                targetId
            )
        )
    }

}
