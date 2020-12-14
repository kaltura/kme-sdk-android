package com.kme.kaltura.kmesdk.ws.message.module

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import kotlinx.android.parcel.Parcelize

class KmeActiveContentModuleMessage<T : KmeActiveContentModuleMessage.ActiveContentPayload> :
    KmeMessage<T>() {

    data class SetActiveContentPayload(val metadata: Metadata) : ActiveContentPayload()

    open class ActiveContentPayload : Payload() {
        @Parcelize
        data class Metadata(
            @SerializedName("playlistFileId") val playlistFileId: String? = null,
            @SerializedName("progress") val progress: Float? = null,
            @SerializedName("caller") val caller: String? = null,
            @SerializedName("activeItem") val activeItem: String? = null,
            @SerializedName("src") val src: String? = null,
            @SerializedName("file_id") val fileId: String? = null,
            @SerializedName("video_url") val videoUrl: String? = null,
            @SerializedName("play_state") val playState: String? = null
        ) : Parcelable

        @SerializedName("content_type")
        val contentType: KmeContentType? = null
    }

}
