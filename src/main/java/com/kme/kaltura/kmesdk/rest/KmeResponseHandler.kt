package com.kme.kaltura.kmesdk.rest

import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    call: suspend () -> T,
    success: (response: T) -> Unit,
    error: (exception: KmeApiException) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val response = call.invoke()

            if (response is KmeResponse && KmeResponse.Status.ERROR == response.status) {
                throw KmeApiException.InternalApiException(response)
            }

            withContext(Dispatchers.Main) {
                success(response)
            }
        } catch (e: Throwable) {
            val appException = when (e) {
                is ConnectException,
                is UnknownHostException -> KmeApiException.NetworkException(cause = e)
                is KmeApiException.InternalApiException -> e
                is KmeApiException.HttpException -> {
                    when (e.errorCode) {
                        in 400..499 -> {
                            KmeApiException.HttpException.ClientException(e.message, e.errorCode, e)
                        }
                        in 500..599 -> KmeApiException.HttpException.ServerException(
                            errorCode = e.errorCode,
                            cause = e
                        )
                        else -> KmeApiException.HttpException(errorCode = e.errorCode, cause = e)
                    }
                }
                else -> KmeApiException.SomethingBadHappenedException(cause = e)
            }
            withContext(Dispatchers.Main) {
                error(appException)
            }
        }
    }

}
