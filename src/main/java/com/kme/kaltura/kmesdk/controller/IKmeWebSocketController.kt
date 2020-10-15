package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent

interface IKmeWebSocketController {

    fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    )

    fun send(message: KmeMessage<out KmeMessage.Payload>)

    fun addListener(listener: IKmeMessageListener)

    fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener)

    fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    )

    fun remove(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    )

    fun removeListener(listener: IKmeMessageListener)

    fun removeListeners()

    fun disconnect()

}
