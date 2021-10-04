package com.kme.kaltura.kmesdk.util

import java.io.Serializable
import kotlin.reflect.KProperty

private object UNINITIALIZED_VALUE

class ResetableLazy<T>(
    private val initializer: () -> T, lock: Any? = null
) : Lazy<T>, Serializable {

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    private val lock = lock ?: this

    fun invalidate() {
        _value = UNINITIALIZED_VALUE
    }

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    _v2 as T
                } else {
                    val typedValue = initializer.invoke()
                    _value = typedValue
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    operator fun setValue(any: Any, property: KProperty<*>, t: T) {
        _value = t
    }
}