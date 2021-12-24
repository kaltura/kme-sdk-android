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
     * @param priority priority for posting events. If more than one listener is queued at a time,
     * events with the higher priority will be posted first.
     */
    fun addListener(
        listener: IKmeMessageListener,
        priority: KmeMessagePriority = KmeMessagePriority.LOW
    )

    /**
     * Add event to listener
     *
     * @param event event to listen
     * @param listener listener for messages
     * @param priority priority for posting events. If more than one listener is queued at a time,
     * events with the higher priority will be posted first.
     */
    fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener,
        priority: KmeMessagePriority = KmeMessagePriority.LOW
    )

    /**
     * Start listen events for listener
     *
     * @param listener listener for messages
     * @param events events to listen
     * @param priority priority for posting events. If more than one listener is queued at a time,
     * events with the higher priority will be posted first.
     */
    fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
        priority: KmeMessagePriority = KmeMessagePriority.LOW
    ): IKmeMessageListener

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
