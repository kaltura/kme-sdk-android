package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

interface IKmeWebSocketController {

    fun isConnected(): Boolean

    fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    )

    fun send(message: KmeMessage<out KmeMessage.Payload>)

    fun disconnect()

}
