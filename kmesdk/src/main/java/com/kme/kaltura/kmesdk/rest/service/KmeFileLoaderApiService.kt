package com.kme.kaltura.kmesdk.rest.service

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * An interface for download files
 */
interface KmeFileLoaderApiService {

    /**
     * Downloads a file from the room
     *
     * @param fileUrl url of a file
     * @return [ResponseBody] object in success case
     */
    @Streaming
    @GET
    suspend fun downloadFile(
        @Url fileUrl: String
    ): ResponseBody?

}
