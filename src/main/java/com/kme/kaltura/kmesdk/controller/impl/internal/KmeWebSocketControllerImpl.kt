package com.kme.kaltura.kmesdk.controller.impl.internal

import android.util.Log
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.IKmeWebSocketController
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.ws.*
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import org.koin.core.inject
import org.koin.core.qualifier.named

internal class KmeWebSocketControllerImpl : KmeController(), IKmeWebSocketController, IKmeWSListener {

    private val TAG = KmeWebSocketControllerImpl::class.java.canonicalName

    private val okHttpClient: OkHttpClient by inject(named("wsOkHttpClient"))
    private val gson: Gson by inject()
    private val webSocketHandler: KmeWebSocketHandler by inject()
    private val messageManager: KmeMessageManager by inject()

    private lateinit var webSocket: WebSocket

    private lateinit var listener: IKmeWSConnectionListener

    private var roomId: Long = 0
    private var companyId: Long = 0

    override fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        this.listener = listener
        this.roomId = roomId
        this.companyId = companyId

        val request = Request.Builder()
            .url(parseWssUrl(url, companyId, roomId, isReconnect, token))
            .build()

        webSocketHandler.listener = this
        webSocket = okHttpClient.newWebSocket(request, webSocketHandler)
    }

    override fun onOpen(response: Response) {
        listener.onOpen()
    }

    override fun onFailure(throwable: Throwable, response: Response?) {
        listener.onFailure(throwable)
    }

    override fun onClosing(code: Int, reason: String) {
        listener.onClosing(code, reason)
    }

    override fun onClosed(code: Int, reason: String) {
        listener.onClosed(code, reason)
    }

    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        val strMessage = gson.toJson(message)
        Log.e(TAG, "send: $strMessage")
        webSocket.send(strMessage)
    }

    override fun disconnect() {
        webSocket.cancel()
        messageManager.removeListeners()
    }

    override fun addListener(listener: IKmeMessageListener) {
        messageManager.addListener(listener)
    }

    override fun addListener(event: KmeMessageEvent, listener: IKmeMessageListener) {
        messageManager.addListener(event, listener)
    }

    override fun listen(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        messageManager.listen(listener, *events)
    }

    override fun remove(listener: IKmeMessageListener, vararg events: KmeMessageEvent) {
        messageManager.remove(listener, *events)
    }

    override fun removeListener(listener: IKmeMessageListener) {
        messageManager.removeListener(listener)
    }

    override fun removeListeners() {
        messageManager.removeListeners()
    }

    private fun parseWssUrl(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String
    ): String {
        return "$url/websocket/inbound-outbound/room?" +
                "room_id=$roomId" +
                "&isReconnect=$isReconnect" +
                "&company_id=$companyId" +
                "&token=$token"
    }

}
