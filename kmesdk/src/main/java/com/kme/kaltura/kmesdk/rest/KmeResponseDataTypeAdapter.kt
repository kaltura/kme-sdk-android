package com.kme.kaltura.kmesdk.rest

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData
import java.lang.reflect.Type

class KmeResponseDataTypeAdapter : JsonDeserializer<KmeResponseData?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): KmeResponseData? {
        return Gson().fromJson(json?.asString, KmeResponseData::class.java)
    }

}
