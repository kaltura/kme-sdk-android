package com.kme.kaltura.kmesdk.rest

sealed class AppException(
    override val message: String?,
    cause: Throwable? = null,
) : Exception(message, cause) {

    class NetworkException(
        message: String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    open class HttpException(
        message: String? = null,
        val errorCode: Int,
        cause: Throwable? = null,
    ) : AppException(message, cause) {

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

    class ParseJsonException(
        message: String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)

    class SomethingBadHappenedException(
        message: String? = null,
        cause: Throwable? = null,
    ) : AppException(message, cause)

}
