package com.kme.kaltura.kmesdk.rest.response.room.notes

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeGetRoomNotesResponse(
    @SerializedName("data") override val data: KmeRoomNotesData?
) : KmeResponse() {

    data class KmeRoomNotesData(
        @SerializedName("notes") val notes: List<KmeRoomNote>?
    ) : KmeResponseData()

}
