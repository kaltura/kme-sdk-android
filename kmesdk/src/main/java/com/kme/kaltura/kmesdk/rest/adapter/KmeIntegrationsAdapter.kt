package com.kme.kaltura.kmesdk.rest.adapter

import com.google.gson.*
import com.kme.kaltura.kmesdk.rest.response.room.KmeIntegrations
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import java.lang.reflect.Type

class KmeIntegrationsAdapter : JsonDeserializer<KmeIntegrations?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): KmeIntegrations? {
        return if (json?.isJsonObject == true) {
           return Gson().fromJson(json, KmeIntegrations::class.java)
        } else {
            null
        }
    }

}
