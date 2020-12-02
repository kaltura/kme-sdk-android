package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeWebRTCServer

interface IKmeRoomController : IKmeWebSocketController, IKmeWebRTCController {

    val roomSettings: KmeWebRTCServer?

    fun getRooms(
        companyId: Long,
        pages: Long,
        limit: Long,
        success: (response: KmeGetRoomsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getRoomInfo(
        alias: String,
        checkPermission: Int,
        withFiles: Int,
        success: (response: KmeGetRoomInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    fun getWebRTCLiveServer(
        roomAlias: String,
        success: (response: KmeGetWebRTCServerResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
