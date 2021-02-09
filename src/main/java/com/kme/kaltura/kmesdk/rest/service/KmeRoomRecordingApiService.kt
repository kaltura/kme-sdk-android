package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeCheckRecordingLicenseResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface for room recording API calls
 */
interface KmeRoomRecordingApiService {

    /**
     * Checking recording license for the room
     *
     * @param roomId id of a room
     * @return [KmeCheckRecordingLicenseResponse] object in success case
     */
    @GET("company/CheckRecordingLicenseToCompanyByRoom")
    suspend fun heckRecordingLicense(
        @Query("room_id") roomId: Long
    ): KmeCheckRecordingLicenseResponse

}
