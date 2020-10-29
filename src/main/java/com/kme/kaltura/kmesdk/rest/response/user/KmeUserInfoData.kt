package com.kme.kaltura.kmesdk.rest.response.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KmeUserInfoData(
    @SerializedName("id") var id: Long?,
    @SerializedName("first_name") var firstName: String?,
    @SerializedName("last_name") var lastName: String?,
    @SerializedName("lang") var lang: String?,
    @SerializedName("avatar") var avatar: String?,
    @SerializedName("userCompanies") var userCompanies: UserCompanies?
) : Parcelable {

    @Parcelize
    data class UserCompanies(
        @SerializedName("owner_company_id") var companyOwnerId: Long?,
        @SerializedName("companies") var companies: List<KmeUserCompany>?,
        @SerializedName("active_company_id") var activeCompanyId: Long?,
    ) : Parcelable

}
