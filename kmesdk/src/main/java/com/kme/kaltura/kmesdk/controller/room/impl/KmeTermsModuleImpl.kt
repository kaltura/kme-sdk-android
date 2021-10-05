package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeTermsModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.terms.KmeGetTermsResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeTermsApiService
import com.kme.kaltura.kmesdk.util.messages.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation actions related to chat
 */
class KmeTermsModuleImpl : KmeController(), IKmeTermsModule {

    private val termsApiService: KmeTermsApiService by inject()

    private val webSocketModule: IKmeWebSocketModule by scopedInject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Set terms condition  agreed or rejected
     */
    override fun setTermsCondition(agree: Boolean) {
        webSocketModule.send(buildTermsAgreementMessage(agree))
    }

    /**
     * Getting terms and condition message
     */
    override fun getTermsMessage(
        companyId: Long,
        success: (response: KmeGetTermsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { termsApiService.getTerms(companyId) },
                success = {
                    success.invoke(it)
                },
                error = {
                    error.invoke(it)
                }
            )
        }
    }
}
