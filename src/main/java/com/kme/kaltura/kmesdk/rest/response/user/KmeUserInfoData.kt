package com.kme.kaltura.kmesdk.rest.response.user

import com.google.gson.annotations.SerializedName

data class KmeUserInfoData(
    var id: Long? = null,
    @SerializedName("first_name")
    var firstName: String? = null,
    @SerializedName("last_name")
    var lastName: String? = null,
    var lang: String? = null,
    var avatar: String? = null,
    var userCompanies: UserCompanies? = null
) {

    data class UserCompanies(
        @SerializedName("owner_company_id")
        var companyOwnerId: Long? = null,
        var companies: List<KmeUserCompany>? = null,
        @SerializedName("active_company_id")
        var activeCompanyId: Long? = null,
    )

}
