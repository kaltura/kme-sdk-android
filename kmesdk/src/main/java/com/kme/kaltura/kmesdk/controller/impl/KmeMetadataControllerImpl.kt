package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.metadata.GetTranslationsResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KeepAliveResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KmeMetadata
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeMetadataApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for actions related to metadata
 */
class KmeMetadataControllerImpl : KmeKoinComponent, IKmeMetadataController {

    private val metadataApiService: KmeMetadataApiService by inject()
    private val prefs: IKmePreferences by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var metadata: KmeMetadata? = null

    /**
     * Getting stored metadata
     */
    override fun getMetadata(): KmeMetadata? {
        return metadata
    }

    /**
     * Getting metadata for global usage
     */
    override fun fetchMetadata(
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { metadataApiService.getMetadata() },
                { response ->
                    metadata = response.data
                    response.data?.csrf?.let {
                        prefs.putString(KmePrefsKeys.CSRF_TOKEN, it)
                    }
                    success()
                },
                error
            )
        }
    }

    /**
     * Keep user alive. Update Csrf token.
     */
    override fun keepAlive(
        success: (response: KeepAliveResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { metadataApiService.keepAlive() },
                success,
                error
            )
        }
    }

    /**
     * Getting translations strings for specific language
     */
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

    /**
     * Getting debug information about session
     */
    override fun sessionInfo(
        success: (response: String) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { metadataApiService.sessionInfo() },
                success,
                error
            )
        }
    }

}
