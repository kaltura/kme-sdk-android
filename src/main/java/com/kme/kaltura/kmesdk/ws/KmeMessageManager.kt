package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

internal class KmeMessageManager {

    private val listeners: MutableMap<KmeMessageEvent?, MutableSet<IKmeMessageListener>?> =
        Collections.synchronizedMap(mutableMapOf())

    private val uiScope = CoroutineScope(Dispatchers.Main)

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

    fun addListener(listener: IKmeMessageListener) {
        addToMap(null, listener, listeners)
    }

    fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        addToMap(event, listener, listeners)
    }

    fun removeListeners() {
        for (listener in listeners.values) {
            listener?.clear()
        }

        listeners.clear()
    }

    fun removeListener(listener: IKmeMessageListener) {
        for (listenerSet in listeners.values) {
            listenerSet?.remove(listener)
        }
    }

    fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    ): IKmeMessageListener {
        for (eventType in events) {
            addListener(eventType, listener)
        }
        return listener
    }

    fun remove(listener: IKmeMessageListener, vararg eventTypes: KmeMessageEvent) {
        for (eventType in eventTypes) {
            val listenerSet: MutableSet<IKmeMessageListener>? = listeners[eventType]
            listenerSet?.remove(listener)
        }
    }

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