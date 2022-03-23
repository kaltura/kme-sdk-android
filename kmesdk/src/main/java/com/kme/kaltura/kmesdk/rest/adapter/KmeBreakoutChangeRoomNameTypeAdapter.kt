package com.kme.kaltura.kmesdk.rest.adapter

import com.google.gson.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.BreakoutChangeNamePayload
import java.lang.reflect.Type

class KmeBreakoutChangeRoomNameTypeAdapter : JsonDeserializer<BreakoutChangeNamePayload> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): BreakoutChangeNamePayload {
        val entries = (json as JsonObject).entrySet()

        val room = JsonObject()
        entries.forEach {
            (it.value as JsonObject).addProperty("room_id", it.key)
            room.add("room", it.value)
        }

        return Gson().fromJson(room, BreakoutChangeNamePayload::class.java)
    }

}
