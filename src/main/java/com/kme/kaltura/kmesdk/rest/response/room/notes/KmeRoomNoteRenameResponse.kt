package com.kme.kaltura.kmesdk.rest.response.room.notes

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

class KmeRoomNoteRenameResponse(
@SerializedName("data") override val data: RoomNoteRenameData?
) : KmeResponse()

data class RoomNoteRenameData(
    @SerializedName("note_type") val noteType: String? = null,
    @SerializedName("note_name") val name: String? = null,
    @SerializedName("date_created") val dateCreated: String? = null,
    @SerializedName("id") val id: Long? = null,
) : KmeResponseData()