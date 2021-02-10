package com.kme.kaltura.kmeapplication.util.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole

inline fun <reified T : Enum<T>> valueOf(type: String?, default: T): T {
    if (type == null) return default
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        default
    }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun String?.parseRoomAlias(): String? {
    val groups = this?.let {
        "room_id=([^&\\n\\t\\s/?]+)|/#/room/([^&\\n\\t\\s/?]+)".toRegex().find(it)?.groups
    }

    return groups?.get(1)?.value ?: groups?.get(2)?.value
}

fun KmeParticipant?.isModerator(): Boolean {
    return this != null
            && (userRole == KmeUserRole.INSTRUCTOR ||
            userRole == KmeUserRole.ADMIN ||
            userRole == KmeUserRole.OWNER ||
            isModerator == true)
}

inline fun <A, B, R> ifNonNull(a: A?, b: B?, block: (a: A, b: B) -> R): R? {
    return if (a != null && b != null) {
        block(a, b)
    } else null
}