package com.kme.kaltura.kmesdk.rest

sealed class RestResult<out V, out E> {

    data class Success<V>(val value: V) : RestResult<V, Nothing>()

    data class Error<E>(val error: E) : RestResult<Nothing, E>()

}
