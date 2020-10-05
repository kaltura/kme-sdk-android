package com.kme.kaltura.kmesdk.rest

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class KmeBooleanTypeAdapter : JsonDeserializer<Boolean> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Boolean {
        val code = json!!.asInt
        return if (code == 0) false else (if (code == 1) true else null)!!
    }

}
