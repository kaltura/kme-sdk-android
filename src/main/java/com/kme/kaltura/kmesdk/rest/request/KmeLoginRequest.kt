package com.kme.kaltura.kmesdk.rest.request

data class KmeLoginRequest(
    val login : String,
    val password : String
) : KmeRequest()
