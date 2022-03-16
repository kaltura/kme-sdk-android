package com.kme.kaltura.kmesdk.module.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.module.IKmeTermsModule
import com.kme.kaltura.kmesdk.module.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.terms.KmeGetTermsResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeTermsApiService
import com.kme.kaltura.kmesdk.util.messages.buildTermsAgreementMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation actions related to terms & conditions
 */
class KmeTermsModuleImpl : KmeController(), IKmeTermsModule {

    private val termsApiService: KmeTermsApiService by inject()

    private val webSocketModule: IKmeWebSocketModule by scopedInject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Set terms condition  agreed or rejected
     */
    override fun setTermsCondition(agree: Boolean, roomId: Long, companyId: Long) {
        webSocketModule.send(buildTermsAgreementMessage(agree, roomId, companyId))
    }

    /**
     * Getting terms and condition message
     */
    override fun getTermsMessage(
        roomId: Long,
        companyId: Long,
        success: (response: KmeGetTermsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { termsApiService.getTerms(roomId, companyId) },
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
