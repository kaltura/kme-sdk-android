package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * An implementation for transferring messages to appropriate listeners
 */
internal class KmeMessageManager {

    private val listeners: MutableMap<KmeMessageEvent?, MutableSet<IKmeMessageListener>?> =
        Collections.synchronizedMap(mutableMapOf())

    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Transfer messages to appropriate listeners
     *
     * @param event event name
     * @param message message to send
     */
    fun post(event: KmeMessageEvent, message: KmeMessage<KmeMessage.Payload>) {
        val postListeners: MutableSet<IKmeMessageListener> = mutableSetOf()
        val eventListeners = listeners[event]
        val allEventListeners = listeners[null]

        eventListeners?.let { postListeners.addAll(it) }
        allEventListeners?.let { postListeners.addAll(it) }

        if (postListeners.isNotEmpty()) {
            uiScope.launch {
                for (postListener in postListeners) {
                    postListener.onMessageReceived(message)
                }
            }
        }
    }

    /**
     * Add listener to the listeners collection
     *
     * @param listener listener to be added
     */
    fun addListener(listener: IKmeMessageListener) {
        addToMap(null, listener, listeners)
    }

    /**
     * Add listener to the listeners collection
     *
     * @param event event to be listed by [listener]
     * @param listener listener to be added
     */
    fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        addToMap(event, listener, listeners)
    }

    /**
     * Clear listeners collection
     */
    fun removeListeners() {
        for (listener in listeners.values) {
            listener?.clear()
        }

        listeners.clear()
    }

    /**
     * Remove specific listener from the listeners collection
     *
     * @param listener listener to be removed
     */
    fun removeListener(listener: IKmeMessageListener) {
        for (listenerSet in listeners.values) {
            listenerSet?.remove(listener)
        }
    }

    /**
     * Listen list of events by specific listener
     *
     * @param listener listener to be added
     * @param events events to be listed by [listener]
     * @return [IKmeMessageListener] as listener object
     */
    fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    ): IKmeMessageListener {
        for (eventType in events) {
            addListener(eventType, listener)
        }
        return listener
    }

    /**
     * Stop listen events for listener
     *
     * @param listener target listener
     * @param eventTypes list of events
     */
    fun remove(listener: IKmeMessageListener, vararg eventTypes: KmeMessageEvent) {
        for (eventType in eventTypes) {
            val listenerSet: MutableSet<IKmeMessageListener>? = listeners[eventType]
            listenerSet?.remove(listener)
        }
    }

    /**
     * Add listener to the listeners collection
     *
     * @param key event name
     * @param listener listener to be added
     * @param map internal collection of listeners
     */
    private fun addToMap(
        key: KmeMessageEvent?,
        listener: IKmeMessageListener,
        map: MutableMap<KmeMessageEvent?, MutableSet<IKmeMessageListener>?>
    ) {
        var listenerSet: MutableSet<IKmeMessageListener>?
        listenerSet = map[key]
        if (listenerSet == null) {
            listenerSet = HashSet<IKmeMessageListener>()
            listenerSet.add(listener)
            map[key] = listenerSet
        } else {
            listenerSet.add(listener)
        }
    }

}