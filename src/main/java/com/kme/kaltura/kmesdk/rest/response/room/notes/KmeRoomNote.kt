package com.kme.kaltura.kmesdk.rest.response.room.notes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeRoomNote(
    @SerializedName("changed_users") val changedUsers: String? = null,
    @SerializedName("company_id") val companyId: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("date_created") val dateCreated: Long? = null,
    @SerializedName("date_modified") var dateModified: Long? = null,
    @SerializedName("deleted") val deleted: String? = null,
    @SerializedName("html") var html: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("last_download_date") val lastDownloadDate: String? = null,
    @SerializedName("last_version") val lastVersion: String? = null,
    @SerializedName("note_name") var name: String? = null,
    @SerializedName("note_type") val type: String? = null,
    @SerializedName("room_id") val roomId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("user_created") val userCreated: String? = null,
    @SerializedName("user_type") val userType: String? = null
) : Parcelable
