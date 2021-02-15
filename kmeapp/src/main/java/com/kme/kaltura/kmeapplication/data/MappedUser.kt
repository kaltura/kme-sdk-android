package com.kme.kaltura.kmeapplication.data

import android.os.Parcelable
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserInfoData
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize

@Parcelize
class MappedUser(
    private val user: KmeUserInfoData?
) : IUser, Parcelable {

    override fun getId() = user?.getUserId().toString()

    override fun getName() =
        if (user?.firstName != null && user.lastName != null) {
            "${user.firstName.toNonNull()} ${user.lastName.toNonNull()}"
        } else {
            (user?.fullName).toNonNull()
        }

    override fun getAvatar() = user?.avatar

}