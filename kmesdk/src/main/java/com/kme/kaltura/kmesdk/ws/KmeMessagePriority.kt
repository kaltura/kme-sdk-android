package com.kme.kaltura.kmesdk.ws

/**
 * Priorities for posting events. If more than one listener is queued at a time, events with the
 * higher priority will be posted first.
 */
enum class KmeMessagePriority(val value: Int) {
    HIGH(0),
    NORMAL(1),
    LOW(2)
}