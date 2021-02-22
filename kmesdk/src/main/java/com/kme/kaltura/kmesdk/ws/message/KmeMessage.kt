package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint

open class KmeMessage<T : KmeMessage.Payload> {

    @SerializedName("module")
    var module: KmeMessageModule? = null

    @SerializedName("name")
    var name: KmeMessageEvent? = null

    @SerializedName("type")
    var type: KmeMessageEventType? = null

    @SerializedName("constraint")
    var constraint: List<KmeConstraint>? = null

    @SerializedName("payload")
    var payload: T? = null

    open class Payload {
        val events: List<String>? = null
    }

}
