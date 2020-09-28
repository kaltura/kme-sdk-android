package com.kme.kaltura.kmesdk.rest

import java.net.ConnectException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(call: suspend () -> T): RestResult<T, AppException> {
    return try {
        RestResult.Success(call.invoke())
    } catch (e: Throwable) {
        val appException = when (e) {
            is ConnectException,
            is UnknownHostException -> AppException.NetworkException(cause = e)
            is AppException.HttpException -> {
                when (e.errorCode) {
                    in 400..499 -> {
                        AppException.HttpException.ClientException(e.message, e.errorCode, e)
                    }
                    in 500..599 -> AppException.HttpException.ServerException(errorCode = e.errorCode, cause = e)
                    else -> AppException.HttpException(errorCode = e.errorCode, cause = e)
                }
            }
            else -> AppException.SomethingBadHappenedException(cause = e)
        }
        RestResult.Error(appException)
    }

}
