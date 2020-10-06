package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.metadata.GetTranslationsResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KmeMetadata

interface IKmeMetadataController {

    fun getMetadata() : KmeMetadata?

    fun fetchMetadata(
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getTranslation(
        lang: String,
        success: (response: GetTranslationsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
