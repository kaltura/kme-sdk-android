package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.SendRoomPasswordPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.TermsAgreementPayload

internal fun buildRoomPasswordMessage(
    roomId: Long,
    companyId: Long,
    password: String
): KmeBannersModuleMessage<SendRoomPasswordPayload> {
    return KmeBannersModuleMessage<SendRoomPasswordPayload>().apply {
        module = KmeMessageModule.BANNERS
        name = KmeMessageEvent.SEND_ROOM_PASSWORD
        type = KmeMessageEventType.CALLBACK
        payload = SendRoomPasswordPayload(roomId, companyId, password)
    }
}

internal fun buildTermsAgreementMessage(
    agree: Boolean?
): KmeBannersModuleMessage<TermsAgreementPayload> {
    return KmeBannersModuleMessage<TermsAgreementPayload>().apply {
        module = KmeMessageModule.BANNERS
        name = KmeMessageEvent.BANNERS_SET_AGREEMENT
//        type = KmeMessageEventType.CALLBACK
        payload = TermsAgreementPayload(agree)
    }
}
