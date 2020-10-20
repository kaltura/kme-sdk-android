package com.kme.kaltura.kmesdk

import com.kme.kaltura.kmesdk.ws.message.KmeMessage

fun String.encryptWith(key: String): String {
    val s = IntArray(256)
    var x: Int

    for (i in 0..255) {
        s[i] = i
    }

    var j = 0
    for (i in 0..255) {
        j = (j + s[i] + key[i % key.length].toInt()) % 256
        x = s[i]
        s[i] = s[j]
        s[j] = x
    }

    var i = 0
    j = 0

    var result = ""
    for (element in this) {
        i = (i + 1) % 256
        j = (j + s[i]) % 256
        x = s[i]
        s[i] = s[j]
        s[j] = x
        result += (element.toInt() xor s[(s[i] + s[j]) % 256]).toChar()
    }
    return result
}

inline fun <reified T> KmeMessage<*>.toType(): T? =
    if (this is T)
        @Suppress("UNCHECKED_CAST")
        this else
        null
