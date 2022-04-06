package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * An implementation for transferring messages to appropriate listeners
 */
internal class KmeMessageManager(
    private val internalDataModule: IKmeInternalDataModule
) : IKmeMessageManager {

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
        postFrom: KmeWebSocketType,
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
                    if (internalDataModule.breakoutRoomId == 0L ||
                        postListener.filter == KmeMessageFilter.MAIN ||
                        postFrom == KmeWebSocketType.BREAKOUT
                    ) {
                        postListener?.listener?.onMessageReceived(message)
                    }
                }
            }
        }
    }

    override fun addListener(
        listener: IKmeMessageListener,
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ) {
        addToMap(null, listener, priority, filter)
    }

    override fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener,
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ) {
        addToMap(event, listener, priority, filter)
    }

    override fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ): IKmeMessageListener {
        for (eventType in events) {
            addListener(eventType, listener, priority, filter)
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
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ) {
        var priorityQueue = listeners[key]

        if (priorityQueue == null) {
            priorityQueue = PriorityQueue()
            val entry = Entry(listener, priority, filter)
            priorityQueue.offer(entry)
            listeners[key] = priorityQueue
        } else {
            val entry = Entry(listener, priority, filter)
            if (!priorityQueue.contains(entry)) {
                priorityQueue.offer(entry)
            }
        }
    }

    inner class Entry(
        val listener: IKmeMessageListener,
        val priority: KmeMessagePriority,
        val filter: KmeMessageFilter
    ) : Comparable<Entry> {

        override fun compareTo(other: Entry): Int {
            return this.priority.value.compareTo(other.priority.value)
        }

        override fun equals(other: Any?): Boolean {
            if (javaClass != other?.javaClass) return false

            other as Entry

            if (listener != other.listener) return false
            if (priority != other.priority) return false
            if (filter != other.filter) return false

            return true
        }

        override fun hashCode(): Int {
            var result = listener.hashCode()
            result = 31 * result + priority.hashCode()
            return result
        }


    }

}