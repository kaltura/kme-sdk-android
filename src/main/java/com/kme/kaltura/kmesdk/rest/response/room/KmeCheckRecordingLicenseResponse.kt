package com.kme.kaltura.kmesdk.rest.response.room

import com.google.gson.annotations.SerializedName
import com.kme.kaltura.kmesdk.rest.response.KmeResponse
import com.kme.kaltura.kmesdk.rest.response.KmeResponseData

data class KmeCheckRecordingLicenseResponse(
    @SerializedName("data") override val data: KmeRecordingLicenseData
) : KmeResponse()

// TODO: ask server side send object instead of string
//{"status":"success","data":""}
data class KmeRecordingLicenseData(
    @SerializedName("value") val value: String?
) : KmeResponseData()