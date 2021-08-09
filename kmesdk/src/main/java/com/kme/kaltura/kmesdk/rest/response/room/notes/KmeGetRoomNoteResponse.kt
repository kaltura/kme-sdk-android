package com.kme.kaltura.kmesdk.rest.response.room.notes

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeGetRoomNoteResponse(
    @SerializedName("data") override val data: KmeRoomNoteData?
) : KmeResponse() {

    data class KmeRoomNoteData(
        @SerializedName("note_data") val note: KmeNoteData?
    ) : KmeResponseData()

    data class KmeNoteData(
        @SerializedName("note_name") val name: String? = null,
        @SerializedName("content") var content: String? = null
    )
}
