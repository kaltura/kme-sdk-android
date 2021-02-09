package com.kme.kaltura.kmesdk.rest.response.room.notes

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

class KmeRoomNoteCreateResponse(
    @SerializedName("data") override val data: KmeRoomNotesCreateData?
) : KmeResponse()

data class KmeRoomNotesCreateData(
    @SerializedName("note_name") val name: String? = null,
    @SerializedName("date_created") val dateCreated: Long? = null,
    @SerializedName("id") val id: Long? = null,
) : KmeResponseData()