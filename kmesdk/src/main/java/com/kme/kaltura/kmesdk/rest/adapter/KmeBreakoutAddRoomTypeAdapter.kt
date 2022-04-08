package com.kme.kaltura.kmesdk.rest.adapter

import com.google.gson.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.BreakoutAddRoomPayload
import java.lang.reflect.Type

class KmeBreakoutAddRoomTypeAdapter : JsonDeserializer<BreakoutAddRoomPayload> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): BreakoutAddRoomPayload {
        val entries = (json as JsonObject).entrySet()

        val room = JsonObject()
        entries.forEach {
            room.add("room", it.value)
        }

        return Gson().fromJson(room, BreakoutAddRoomPayload::class.java)
    }

}
