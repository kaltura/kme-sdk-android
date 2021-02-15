package com.kme.kaltura.kmesdk.rest.response.room.notes

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

class KmeDeleteRoomNoteResponse(
    @SerializedName("data") override val data: KmeResponseData?
) : KmeResponse()
