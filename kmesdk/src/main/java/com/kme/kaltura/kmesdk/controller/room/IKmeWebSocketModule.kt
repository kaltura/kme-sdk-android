package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

/**
 * An interface for communication with socket in the room
 */
interface IKmeWebSocketModule : IKmeModule {

    /**
     * Check is socket connected
     *
     * @return 'true' in case socket is connected
     */
    fun isConnected(): Boolean

    /**
     * Establish socket connection
     *
     * @param url url of a destination
     * @param companyId id of a company
     * @param roomId id of a room
     * @param isReconnect reconnection flag
     * @param token room auth token
     * @param listener connection listener
     */
    fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    )

    /**
     * Send message via socket
     *
     * @param message [KmeMessage] object with specific payload type
     */
    fun send(message: KmeMessage<out KmeMessage.Payload>)

    /**
     * Disconnect socket connection
     */
    fun disconnect()

}
