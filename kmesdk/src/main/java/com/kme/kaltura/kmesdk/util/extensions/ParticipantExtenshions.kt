package com.kme.kaltura.kmesdk.util.extensions

import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole

/**
 * Check participant is moderator
 */
 fun KmeParticipant?.isModerator(): Boolean {
    return this != null
            && (userRole == KmeUserRole.INSTRUCTOR ||
            userRole == KmeUserRole.ADMIN ||
            userRole == KmeUserRole.OWNER ||
            isModerator == true)
}