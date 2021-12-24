package com.kme.kaltura.kmesdk.ws

import android.util.Log
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * An implementation for transferring messages to appropriate listeners
 */
internal class KmeMessageManager : IKmeMessageManager {

    private val listeners: MutableMap<KmeMessageEvent?, PriorityQueue<Entry>?> =
        Collections.synchronizedMap(mutableMapOf())

    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Transfer messages to appropriate listeners
     *
     * @param event event name
     * @param message message to send
     */
    fun post(
        event: KmeMessageEvent,
        message: KmeMessage<KmeMessage.Payload>
    ) {
        val postListeners = PriorityQueue<Entry>()
        val eventListeners = listeners[event]
        val allEventListeners = listeners[null]

        eventListeners?.let { postListeners.addAll(it) }
        allEventListeners?.let { postListeners.addAll(it) }

        if (postListeners.isNotEmpty()) {
            uiScope.launch {
                for (postListener in postListeners) {
                    Log.e("TAG2", "post: ${postListener.listener}", )
                    postListener?.listener?.onMessageReceived(message)
                }
            }
        }
    }

    override fun addListener(
        listener: IKmeMessageListener,
        priority: KmeMessagePriority
    ) {
        addToMap(null, listener, priority)
    }

    override fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener,
        priority: KmeMessagePriority
    ) {
        addToMap(event, listener, priority)
    }

    override fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
        priority: KmeMessagePriority
    ): IKmeMessageListener {
        for (eventType in events) {
            addListener(eventType, listener, priority)
        }
        return listener
    }

    override fun remove(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    ) {
        for (eventType in events) {
            val priorityQueue: PriorityQueue<Entry>? = listeners[eventType]
            priorityQueue?.removeAll { entry -> entry.listener == listener }
        }
    }

    override fun removeListener(listener: IKmeMessageListener) {
        for (priorityQueue in listeners.values) {
            priorityQueue?.removeAll { entry -> entry.listener == listener }
        }
    }

    override fun removeListeners() {
        for (listener in listeners.values) {
            listener?.clear()
        }
        listeners.clear()
    }

    /**
     * Add listener to the listeners collection
     *
     * @param key event name
     * @param listener listener to be added
     */
    private fun addToMap(
        key: KmeMessageEvent?,
        listener: IKmeMessageListener,
        priority: KmeMessagePriority
    ) {
        var priorityQueue = listeners[key]

        if (priorityQueue == null) {
            priorityQueue = PriorityQueue()
            val entry = Entry(listener, priority)
            priorityQueue.offer(entry)
            listeners[key] = priorityQueue
        } else {
            val entry = Entry(listener, priority)
            priorityQueue.offer(entry)
        }
    }

    inner class Entry(
        val listener: IKmeMessageListener,
        val priority: KmeMessagePriority
    ) : Comparable<Entry> {

        override fun compareTo(other: Entry): Int {
            return this.priority.value.compareTo(other.priority.value)
        }

    }

}