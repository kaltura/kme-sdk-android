package com.kme.kaltura.kmesdk.rest

import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Main REST function. Sending requests and handle responses
 */
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
            e.printStackTrace()
            val appException = when (e) {
                is ConnectException,
                is UnknownHostException -> KmeApiException.NetworkException(cause = e)
                is KmeApiException.InternalApiException -> e
                is KmeApiException.HttpException, is HttpException -> {
                    val errorCode = if (e is KmeApiException.HttpException)
                        e.errorCode
                    else
                        (e as HttpException).code()

                    when (errorCode) {
                        in 400..499 -> {
                            KmeApiException.HttpException.ClientException(e.message, errorCode, e)
                        }
                        in 500..599 -> KmeApiException.HttpException.ServerException(
                            errorCode = errorCode,
                            cause = e
                        )
                        else -> KmeApiException.HttpException(errorCode = errorCode, cause = e)
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
