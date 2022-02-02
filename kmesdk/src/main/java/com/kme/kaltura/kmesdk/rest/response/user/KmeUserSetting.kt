package com.kme.kaltura.kmesdk.rest.response.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeChatModule
import com.kme.kaltura.kmesdk.rest.response.room.settings.KmeParticipantsModule
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.parcelize.Parcelize

@Parcelize
class KmeUserSetting(
    var qnaChat: KmePermissionValue? = KmePermissionValue.ON,
    var publicChat: KmePermissionValue? = KmePermissionValue.ON,
    var participants: KmePermissionValue? = KmePermissionValue.ON,
) : Parcelable
