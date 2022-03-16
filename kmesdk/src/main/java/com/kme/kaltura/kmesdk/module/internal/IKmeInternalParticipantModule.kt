package com.kme.kaltura.kmesdk.module.internal

import com.kme.kaltura.kmesdk.module.IKmeModule
import com.kme.kaltura.kmesdk.module.IKmeParticipantModule
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant

/**
 * An interface for wrap actions with [IKmeParticipantModule]
 * Only for internal SDK usage. This API not visible to the app level.
 */
internal interface IKmeInternalParticipantModule : IKmeParticipantModule, IKmeModule {

    /**
     * Subscribing for the room events related to participants
     */
    fun subscribe()

    /**
     * Update participants roomId in case breakout assignments
     *
     * @param notify indicates application level update
     */
    fun updateParticipantsRoomId(notify: Boolean = true)

    /**
     * Setting initialization data to the module
     *
     * @param participant to update
     * @param isSpeaking speaking state
     */
    fun participantSpeaking(
        participant: KmeParticipant,
        isSpeaking: Boolean
    )

}
