package com.kme.kaltura.kmesdk.ws.message.module

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.KmeFileType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import kotlinx.android.parcel.Parcelize

class KmeActiveContentModuleMessage<T : KmeActiveContentModuleMessage.ActiveContentPayload> :
    KmeMessage<T>() {

    @Parcelize
    data class SetActiveContentPayload(val metadata: Metadata) : ActiveContentPayload()

    @Parcelize
    open class ActiveContentPayload : Payload(), Parcelable {
        @Parcelize
        data class Metadata(
            @SerializedName("playlistFileId") val playlistFileId: String? = null,
            @SerializedName("progress") val progress: Float? = null,
            @SerializedName("caller") val caller: String? = null,
            @SerializedName("activeItem") val activeItem: String? = null,
            @SerializedName("src") val src: String? = null,
            @SerializedName("file_id") val fileId: String? = null,
            @SerializedName("current_slide") val currentSlide: Int? = null,
            @SerializedName("file_type") val fileType: KmeFileType? = null,
            @SerializedName("slides") val slides: List<Slide>? = null,
            @SerializedName("video_url") val videoUrl: String? = null,
            @SerializedName("play_state") val playState: String? = null,
            @SerializedName("boards") val boards: List<Board>? = null,
            @SerializedName("pages") val pages: List<Page>? = null,
            @SerializedName("board_id") val boardId: String? = null,
            @SerializedName("entryId") val entryId: String? = null,
            @SerializedName("ks") val ks: String? = null,
            @SerializedName("active_page") val activePageId: String? = null
        ) : Parcelable

        @Parcelize
        data class Slide(
            @SerializedName("slide_number") val slideNumber: String? = null,
            @SerializedName("audio_clips") val audioClips: String? = null,
            @SerializedName("url") val url: String? = null,
            @SerializedName("thumbnail") val thumbnail: String? = null,
            var isSelected: Boolean
        ) : Parcelable

        @Parcelize
        data class Board(
            @SerializedName("id") val id: String? = null,
            @SerializedName("name") val name: String? = null,
            @SerializedName("active_page") val activePageId: String? = null
        ) : Parcelable

        @Parcelize
        data class Page(
            @SerializedName("id") val id: String? = null,
            @SerializedName("thumbnail") val thumbnail: String? = null,
            @SerializedName("date_created") val createdDate: Long? = null,
            @SerializedName("background_metadata") var backgroundMetadata: KmeWhiteboardBackgroundType? = null
        ) : Parcelable

        @SerializedName("content_type")
        val contentType: KmeContentType? = null
    }

}
