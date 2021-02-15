package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.ws.message.KmeMessage

/**
 * An interface for incoming messages via socket related to the room
 */
interface IKmeMessageListener {

    /**
     * Incoming message via socket
     *
     * @param message parsed object as incoming message
     */
    fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>)

}
