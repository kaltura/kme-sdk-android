package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent

/**
 * An interface for
 */
interface IKmeMessageManager {

    /**
     * Add listeners for socket messages
     *
     * @param listener listener for messages
     */
    fun addListener(listener: IKmeMessageListener)

    /**
     * Add event to listener
     *
     * @param event event to listen
     * @param listener listener for messages
     */
    fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener
    )

    /**
     * Start listen events for listener
     *
     * @param listener listener for messages
     * @param events events to listen
     */
    fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    ) : IKmeMessageListener

    /**
     * Stop listen events for listener
     *
     * @param listener listener for messages
     * @param events events to stop listen
     */
    fun remove(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    )

    /**
     * Remove listener
     *
     * @param listener listener for messages
     */
    fun removeListener(listener: IKmeMessageListener)

    /**
     * Remove all attached listeners
     */
    fun removeListeners()

}
