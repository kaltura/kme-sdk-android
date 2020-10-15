package com.kme.kaltura.kmesdk.ws

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeRoomInitMessage
import com.kme.kaltura.kmesdk.ws.message.KmeRoomInitMessage.*

private const val KEY_NAME = "name"

internal class KmeMessageParser(
    private val gson: Gson,
    private val jsonParser: JsonParser
) {

    fun parse(messageText: String): KmeMessage<KmeMessage.Payload>? {
        var parsedMessage: KmeMessage<KmeMessage.Payload>? = null
        try {
            val jsonObject = jsonParser.parse(messageText).asJsonObject
            if (jsonObject.has(KEY_NAME)) {
                val name = jsonObject.get(KEY_NAME).asString.toLowerCase()
                parsedMessage = parseMessage(name, messageText)
            }
        } catch (e: Exception) {
            parsedMessage = null
            e.printStackTrace()
        }

        //Unknown message type
        return parsedMessage
    }


    private fun parseMessage(name: String, text: String): KmeMessage<KmeMessage.Payload>? {
        return when (name) {
            KmeMessageEvent.INSTRUCTOR_IS_OFFLINE.toString() -> {
                text.jsonToObject<KmeRoomInitMessage<InstructorIsOfflinePayload>>()
            }
            KmeMessageEvent.JOINED_ROOM.toString() -> {
                text.jsonToObject<KmeRoomInitMessage<JoinedRoomPayload>>()
            }
            KmeMessageEvent.ROOM_STATE.toString() -> {
                text.jsonToObject<KmeRoomInitMessage<RoomStatePayload>>()
            }
            KmeMessageEvent.CLOSE_WEB_SOCKET.toString() -> {
                text.jsonToObject<KmeRoomInitMessage<CloseWebSocketPayload>>()
            }

            else -> null
        }
    }

    private inline fun <reified T> String.jsonToObject(): KmeMessage<KmeMessage.Payload>? {
        return gson.fromJson(this, object : TypeToken<T>() {}.type)
    }

}
