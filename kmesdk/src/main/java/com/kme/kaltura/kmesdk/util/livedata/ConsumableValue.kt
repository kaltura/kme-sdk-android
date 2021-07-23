package com.kme.kaltura.kmesdk.util.livedata

@Deprecated("Use LiveData.toSingleEvent() extension for event-based communication")
class ConsumableValue<T>(private val data: T) {

    private var consumed = false

    fun consume(block: ConsumableValue<T>.(T) -> Unit) {
        if (!consumed) {
            consumed = true
            block(data)
        }
    }

}