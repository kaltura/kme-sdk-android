package com.kme.kaltura.kmesdk.controller.room.internal

import com.kme.kaltura.kmesdk.controller.room.IKmeModule
import com.kme.kaltura.kmesdk.controller.room.IKmeParticipantModule

/**
 * An interface for actions with participants
 */
interface IKmeParticipantsInternalModule : IKmeParticipantModule, IKmeModule {

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