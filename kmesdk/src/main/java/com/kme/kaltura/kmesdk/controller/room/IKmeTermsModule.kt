package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.terms.KmeGetTermsResponse

/**
 * An interface for actions with notes
 */
interface IKmeTermsModule : IKmeModule {

    /**
     * Set terms condition  agreed or rejected
     *
     * @param agree  parameter for agreed or rejected
     */
    fun setTermsCondition(
        agree: Boolean,
    )

    /**
     * Getting terms and condition message
     *
     * @param companyId id of a company
     * @param success function to handle success result. Contains [KmeGetTermsResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun getTermsMessage(
        companyId: Long,
        success: (response: KmeGetTermsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )
}
