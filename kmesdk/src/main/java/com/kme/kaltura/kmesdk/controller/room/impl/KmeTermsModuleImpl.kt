package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.room.IKmeTermsModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.terms.KmeGetTermsResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeTermsApiService
import com.kme.kaltura.kmesdk.util.messages.buildTermsAgreementMessage
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.KmeMessagePriority
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation actions related to terms & conditions
 */
class KmeTermsModuleImpl : KmeController(), IKmeTermsModule {

    private val termsApiService: KmeTermsApiService by inject()

    private val roomController: IKmeRoomController by scopedInject()
    private val webSocketModule: IKmeWebSocketModule by scopedInject()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var listener: IKmeTermsModule.KmeTermsListener? = null

    /**
     * Subscribing for the room events related to terms & conditions
     */
    override fun subscribe(listener: IKmeTermsModule.KmeTermsListener) {
        this.listener = listener

        roomController.listen(
            bannersHandler,
            KmeMessageEvent.TERMS_NEEDED,
            KmeMessageEvent.TERMS_AGREED,
            KmeMessageEvent.TERMS_REJECTED,
            KmeMessageEvent.SET_TERMS_AGREEMENT,
            priority = KmeMessagePriority.NORMAL
        )
    }

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


    private val bannersHandler = object : IKmeMessageListener {
        override fun onMessageReceived(message: KmeMessage<KmeMessage.Payload>) {
            when (message.name) {
                KmeMessageEvent.TERMS_NEEDED -> {
                    listener?.onTermsNeeded()
                }
                KmeMessageEvent.TERMS_AGREED -> {
                    listener?.onTermsAccepted()
                }
                KmeMessageEvent.TERMS_REJECTED -> {
                    listener?.onTermsRejected()
                }
                KmeMessageEvent.SET_TERMS_AGREEMENT -> {
                }
            }
        }
    }

}
