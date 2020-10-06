package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.metadata.GetTranslationsResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KmeMetadata
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeMetadataApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeMetadataControllerImpl : KmeKoinComponent, IKmeMetadataController {

    private val metadataApiService: KmeMetadataApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var metadata: KmeMetadata? = null

    override fun getMetadata(): KmeMetadata? {
        return metadata
    }

    override fun fetchMetadata(
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { metadataApiService.getMetadata() },
                {
                    metadata = it.data
                    success()
                },
                error
            )
        }
    }

    override fun getTranslation(
        lang: String,
        success: (response: GetTranslationsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { metadataApiService.getTranslation(lang) },
                success,
                error
            )
        }
    }

}
