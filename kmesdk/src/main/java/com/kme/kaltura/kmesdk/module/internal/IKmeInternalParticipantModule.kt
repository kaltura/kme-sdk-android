package com.kme.kaltura.kmesdk.module.internal

import com.kme.kaltura.kmesdk.module.IKmeModule
import com.kme.kaltura.kmesdk.module.IKmeParticipantModule

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
     */
    fun updateParticipantsRoomId(notify: Boolean = true)

    /**
     * Setting initialization data to the module
     *
     * @param listener
     * @param listener
     */
    fun participantSpeaking(
        id: Long,
        isSpeaking: Boolean
    )

}
