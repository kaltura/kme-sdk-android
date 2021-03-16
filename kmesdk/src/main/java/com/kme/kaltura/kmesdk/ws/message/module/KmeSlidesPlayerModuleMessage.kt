package com.kme.kaltura.kmesdk.ws.message.module

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage

class KmeSlidesPlayerModuleMessage<T : KmeSlidesPlayerModuleMessage.SlidePlayerPayload> :
    KmeMessage<T>() {

    data class SlideChangedPayload(
        @SerializedName("next_active_slide") val nextActiveSlide: Int
    ) : SlidePlayerPayload()

    data class AnnotationStateChangedPayload(
        @SerializedName("annotations_enabled") val annotationsEnabled: Boolean
    ) : SlidePlayerPayload()

    open class SlidePlayerPayload : Payload()

}
