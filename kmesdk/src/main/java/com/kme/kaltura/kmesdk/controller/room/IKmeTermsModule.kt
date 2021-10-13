package com.kme.kaltura.kmesdk.controller.room

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.terms.KmeGetTermsResponse
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType

/**
 * An interface for actions with notes
 */
interface IKmeTermsModule : IKmeModule {

    /**
     * Subscribing for the room events related to terms & conditions
     */
    fun subscribe(listener: KmeTermsListener)

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
        roomId: Long,
        companyId: Long,
        success: (response: KmeGetTermsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )


    /**
     *  Terms listener
     */
    interface KmeTermsListener {

        /**
         * Callback fired once when terms needed
         */
        fun onTermsNeeded()

        /**
         * Callback fired once when terms accepted
         */
        fun onTermsAccepted()

        /**
         * Callback fired once when terms rejected
         */
        fun onTermsRejected()

    }
}
