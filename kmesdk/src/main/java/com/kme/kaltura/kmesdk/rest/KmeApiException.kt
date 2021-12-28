package com.kme.kaltura.kmesdk.rest

import com.kme.kaltura.kmesdk.rest.response.KmeResponse

/**
 * Main REST exceptions class. Indicates an error produced by REST response handling
 */
sealed class KmeApiException(
    override val message: String?,
    var code: Int = 0,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NetworkException(
        message: String? = null,
        cause: Throwable? = null,
    ) : KmeApiException(message, 0, cause)

    open class HttpException(
        message: String? = null,
        val errorCode: Int,
        cause: Throwable? = null,
    ) : KmeApiException(message, errorCode, cause) {

        class ClientException(
            message: String? = null,
            errorCode: Int,
            cause: Throwable? = null,
        ) : HttpException(message, errorCode, cause)

        class ServerException(
            message: String? = null,
            errorCode: Int,
            cause: Throwable? = null,
        ) : HttpException(message, errorCode, cause)
    }

    class InternalApiException(
        errorResponse: KmeResponse
    ) : KmeApiException(
        errorResponse.data?.message,
        errorResponse.data?.code ?: 0,
        null
    )

    class ParseJsonException(
        message: String? = null,
        cause: Throwable? = null,
    ) : KmeApiException(message, 0, cause)

    class SomethingBadHappenedException(
        message: String? = null,
        cause: Throwable? = null,
    ) : KmeApiException(message, 0, cause)

    class AppAccessException(
        message: String? = null,
        cause: Throwable? = null,
    ) : KmeApiException(message, 0, cause)

    class AppVersionException(
        message: String? = null,
        cause: Throwable? = null,
    ) : KmeApiException(message, 0, cause)

}
