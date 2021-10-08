package com.kme.kaltura.kmesdk.rest.adapter

import com.google.gson.*
import com.kme.kaltura.kmesdk.ws.message.module.KmeBreakoutModuleMessage.BreakoutRoomState
import java.lang.reflect.Type

class KmeBreakoutRoomTypeAdapter : JsonDeserializer<BreakoutRoomState> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): BreakoutRoomState {
        mapRooms(json)
        mapAssignments(json, type = "assignments")
        mapAssignments(json, type = "failed_assignments")
        mapAssignments(json, type = "removed_assignments")

        return Gson().fromJson(json, BreakoutRoomState::class.java)
    }

    private fun mapRooms(json: JsonElement?) {
        val breakoutRooms = (json as JsonObject).get("breakout_rooms")
        val breakoutRoomsEntries = (breakoutRooms as JsonObject).entrySet()

        val breakoutRoomsArray = JsonArray()
        breakoutRoomsEntries.forEach {
            (it.value as JsonObject).addProperty("room_id", it.key)
            breakoutRoomsArray.add(it.value as JsonObject)
        }

        json.remove("breakout_rooms")
        json.add("breakout_rooms", breakoutRoomsArray)
    }

    private fun mapAssignments(json: JsonElement?, type: String) {
        val assignmentsArray = JsonArray()
        (json as JsonObject).get(type)?.let { original ->
            val entries = (original as JsonObject).entrySet()
            if (!entries.isNullOrEmpty()) {
                entries.forEach {
                    assignmentsArray.add(it.value as JsonObject)
                }
            }
            json.remove(type)
        }
        json.add(type, assignmentsArray)
    }

}
