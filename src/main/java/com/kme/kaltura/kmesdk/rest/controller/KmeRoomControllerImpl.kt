package com.kme.kaltura.kmesdk.rest.controller

import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeRoomControllerImpl : KmeKoinComponent, IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun getRooms(
        accessToken: String,
        companyId: Long,
        pages: Long,
        limit: Long,
        success: (response: KmeGetRoomsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRooms(accessToken, companyId, pages, limit) },
                success,
                error
            )
        }
    }

}
