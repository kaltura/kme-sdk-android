package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.ws.message.KmeMessage

interface IKmeMessageListener {

    fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>)

}