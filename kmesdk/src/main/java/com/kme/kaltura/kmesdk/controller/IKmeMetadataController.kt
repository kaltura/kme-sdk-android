package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.metadata.GetTranslationsResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KeepAliveResponse
import com.kme.kaltura.kmesdk.rest.response.metadata.KmeMetadata

/**
 * An interface for actions related to metadata
 */
interface IKmeMetadataController {

    /**
     * Getting stored metadata
     *
     * @return [KmeMetadata] object in success case
     */
    fun getMetadata() : KmeMetadata?

    /**
     * Getting metadata for global usage
     *
     * @param success function to handle success result
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun fetchMetadata(
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Keep user alive. Update Csrf token.
     *
     * @param success function to handle success result. Contains [KeepAliveResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun keepAlive(
        success: (response: KeepAliveResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting translations strings for specific language
     *
     * @param lang language for translations
     * @param success function to handle success result. Contains [GetTranslationsResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getTranslation(
        lang: String,
        success: (response: GetTranslationsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Getting debug information about session
     */
    fun sessionInfo(
        success: (response: String) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
