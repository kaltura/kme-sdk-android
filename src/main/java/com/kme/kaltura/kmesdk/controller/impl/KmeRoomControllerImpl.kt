package com.kme.kaltura.kmesdk.controller.impl

import com.kme.kaltura.kmesdk.controller.IKmeRoomController
import com.kme.kaltura.kmesdk.controller.IKmeWebRTCController
import com.kme.kaltura.kmesdk.controller.IKmeWebSocketController
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomInfoResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetRoomsResponse
import com.kme.kaltura.kmesdk.rest.response.room.KmeGetWebRTCServerResponse
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeRoomApiService
import com.kme.kaltura.kmesdk.webrtc.peerconnection.IKmePeerConnectionClientEvents
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.IKmeMessageListener
import com.kme.kaltura.kmesdk.ws.IKmeWSConnectionListener
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

class KmeRoomControllerImpl : KmeController(), IKmeRoomController {

    private val roomApiService: KmeRoomApiService by inject()
    private val webSocketController: IKmeWebSocketController by inject()
    private val webRTCController: IKmeWebRTCController by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun getRooms(
        companyId: Long,
        pages: Long,
        limit: Long,
        success: (response: KmeGetRoomsResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRooms(companyId, pages, limit) },
                success,
                error
            )
        }
    }

    override fun getRoomInfo(
        alias: String,
        checkPermission: Int,
        withFiles: Int,
        success: (response: KmeGetRoomInfoResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getRoomInfo(alias, checkPermission, withFiles) },
                success,
                error
            )
        }
    }

    override fun getWebRTCLiveServer(
        roomAlias: String,
        success: (response: KmeGetWebRTCServerResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { roomApiService.getWebRTCLiveServer(roomAlias) },
                success,
                error
            )
        }
    }

    override fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        webSocketController.connect(url, companyId, roomId, isReconnect, token, listener)
    }

    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        webSocketController.send(message)
    }

    override fun addListener(listener: IKmeMessageListener) {
        webSocketController.addListener(listener)
    }

    override fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        webSocketController.addListener(event, listener)
    }

    override fun listen(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        webSocketController.listen(listener, *events)
    }

    override fun remove(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        webSocketController.remove(listener, *events)
    }

    override fun removeListener(listener: IKmeMessageListener) {
        webSocketController.removeListener(listener)
    }

    override fun removeListeners() {
        webSocketController.removeListeners()
    }

    override fun disconnect() {
        webSocketController.disconnect()
    }

    override fun createPeerConnection(
        localRenderer: KmeSurfaceRendererView,
        remoteRenderer: KmeSurfaceRendererView,
        turnUrl: String,
        turnUser: String,
        turnCred: String,
        listener: IKmePeerConnectionClientEvents
    ) {
        webRTCController.createPeerConnection(
            localRenderer,
            remoteRenderer,
            turnUrl,
            turnUser,
            turnCred,
            listener
        )
    }

    override fun createOffer() {
        webRTCController.createOffer()
    }

    override fun createAnswer() {
        webRTCController.createAnswer()
    }

    override fun setAnswer(sdp: String) {
        webRTCController.setAnswer(sdp)
    }

    override fun enableCamera(isEnable: Boolean) {
        webRTCController.enableCamera(isEnable)
    }

    override fun enableAudio(isEnable: Boolean) {
        webRTCController.enableAudio(isEnable)
    }

    override fun switchCamera() {
        webRTCController.switchCamera()
    }

    override fun addRenderer() {
        webRTCController.addRenderer()
    }

    override fun disconnectPeerConnection() {
        webRTCController.disconnectPeerConnection()
    }

}
