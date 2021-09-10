package com.kme.kaltura.kmesdk.rest.adapter

import com.google.gson.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.BreakoutChangeRoomNamePayload
import java.lang.reflect.Type

class KmeBreakoutChangeRoomNameTypeAdapter : JsonDeserializer<BreakoutChangeRoomNamePayload> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): BreakoutChangeRoomNamePayload {
        val entries = (json as JsonObject).entrySet()

        val room = JsonObject()
        entries.forEach {
            (it.value as JsonObject).addProperty("room_id", it.key)
            room.add("room", it.value)
        }

        return Gson().fromJson(room, BreakoutChangeRoomNamePayload::class.java)
    }

}
