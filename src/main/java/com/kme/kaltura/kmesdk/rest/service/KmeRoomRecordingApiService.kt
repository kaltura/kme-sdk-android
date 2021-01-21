package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KmeRoomRecordingApiService {

    @GET("company/CheckRecordingLicenseToCompanyByRoom")
    suspend fun heckRecordingLicense(
        @Query("room_id") roomId: Long
    ): KmeCheckRecordingLicenseResponse

}
