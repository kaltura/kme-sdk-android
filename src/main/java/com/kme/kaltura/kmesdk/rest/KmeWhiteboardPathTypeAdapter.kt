package com.kme.kaltura.kmesdk.rest

import com.google.gson.*
import com.kme.kaltura.kmesdk.ws.message.whiteboard.KmeWhiteboardPath
import java.lang.reflect.Type

class KmeWhiteboardPathTypeAdapter : JsonDeserializer<KmeWhiteboardPath?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): KmeWhiteboardPath? {
        val parser = JsonParser()
        val result = parser.parse(json?.asString)?.asJsonArray
        return if (result?.size() ?: 0 > 1) {
            val pathType: String? = result?.get(0)?.asString
            val path: KmeWhiteboardPath? =
                Gson().fromJson(result?.get(1)?.asJsonObject, KmeWhiteboardPath::class.java)
            path?.pathType = pathType

            path
        } else {
            null
        }
    }

}
