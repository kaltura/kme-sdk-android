package com.kme.kaltura.kmesdk.rest.response.user

import com.google.gson.annotations.SerializedName

data class KmeUserInfoData(
    @SerializedName("id") var id: Long?,
    @SerializedName("first_name") var firstName: String?,
    @SerializedName("last_name") var lastName: String?,
    @SerializedName("lang") var lang: String?,
    @SerializedName("avatar") var avatar: String?,
    @SerializedName("userCompanies") var userCompanies: UserCompanies?
) {

    data class UserCompanies(
        @SerializedName("owner_company_id") var companyOwnerId: Long?,
        @SerializedName("companies") var companies: List<KmeUserCompany>?,
        @SerializedName("active_company_id") var activeCompanyId: Long?,
    )

}
