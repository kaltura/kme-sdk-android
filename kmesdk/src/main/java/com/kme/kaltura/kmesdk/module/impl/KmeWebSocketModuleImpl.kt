package com.kme.kaltura.kmesdk.module.impl

import android.util.Log
import com.google.gson.Gson
import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.logger.IKmeLogger
import com.kme.kaltura.kmesdk.module.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.ws.*
import com.kme.kaltura.kmesdk.ws.IKmeWSListener
import com.kme.kaltura.kmesdk.ws.KmeMessageManager
import com.kme.kaltura.kmesdk.ws.KmeWebSocketHandler
import com.kme.kaltura.kmesdk.ws.message.KmeMessage
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import org.koin.core.inject
import java.net.UnknownHostException

/**
 * An implementation for socket actions
 */
internal class KmeWebSocketModuleImpl(
    private val okHttpClient: OkHttpClient,
    private val webSocketHandler: KmeWebSocketHandler,
    private val webSocketType: KmeWebSocketType
) : KmeController(), IKmeWebSocketModule, IKmeWSListener {

    private val gson: Gson by inject()
    private val logger: IKmeLogger by inject()

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val reconnectionScope = CoroutineScope(Dispatchers.IO)

    private var webSocket: WebSocket? = null

    private lateinit var request: Request

    private var listener: IKmeWSConnectionListener? = null
    private var roomId: Long = 0

    private var companyId: Long = 0

    private var reconnectionAttempts = 0
    private var allowReconnection = true
    private var isSocketConnected = false

    private var reconnectionJob: Job? = null

    /**
     * Establish socket connection
     */
    override fun connect(
        url: String,
        companyId: Long,
        roomId: Long,
        isReconnect: Boolean,
        token: String,
        listener: IKmeWSConnectionListener
    ) {
        if (isConnected()) {
            disconnect()
        }

        this.listener = listener
        this.roomId = roomId
        this.companyId = companyId
        this.allowReconnection = true

        request = Request.Builder()
            .url(parseWssUrl(url, companyId, roomId, isReconnect, token))
            .build()

        webSocketHandler.listener = this
        webSocket = newWebSocket()
    }

    /**
     * Creating a new socket object
     */
    private fun newWebSocket() = okHttpClient.newWebSocket(request, webSocketHandler)

    /**
     * Trying to connect socket again if need
     */
    private fun reconnect() {
        Log.e("TAG", "reconnect: allowReconnection = $allowReconnection, reconnectionAttempts = $reconnectionAttempts", )
        if (allowReconnection && reconnectionAttempts < RECONNECTION_ATTEMPTS) {
            reconnectionJob?.cancel()
            reconnectionJob = reconnectionScope.launch {
                delay(5000)
                webSocket?.cancel()
                webSocket = newWebSocket()
                reconnectionAttempts++
            }
        } else {
            disconnect()
            uiScope.launch {
                listener?.onClosed(KmeWebSocketCode.CLOSE_NORMAL.code, "")
            }
        }
    }

    /**
     * Check is socket connected
     */
    override fun isConnected() = isSocketConnected

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    override fun onOpen(response: Response) {
        Log.e("TAG", "onOpen: wsListener = $listener", )
        isSocketConnected = true
        reconnectionJob?.cancel()
        reconnectionAttempts = 0
        uiScope.launch {
            listener?.onOpen()
        }
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    override fun onFailure(throwable: Throwable, response: Response?) {
        Log.e(TAG, "onFailure: $throwable, $response", )
        isSocketConnected = false

        if (throwable !is UnknownHostException || webSocketType == KmeWebSocketType.MAIN) {
            reconnect()
        }
        uiScope.launch {
            if (allowReconnection) {
                listener?.onFailure(throwable)
            }
        }
        if (!allowReconnection) {
            onClosed(KmeWebSocketCode.CLOSE_NORMAL.code, "")
        }
    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    override fun onClosing(code: Int, reason: String) {
        Log.e("TAG", "onClosing: code: $code, reason: $reason")
        isSocketConnected = false
        //Status code == 1000 - normal closure, the connection successfully completed
        if (code != KmeWebSocketCode.CLOSE_NORMAL.code) {
            reconnect()
        }
        uiScope.launch {
            listener?.onClosing(code, reason)
        }
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    override fun onClosed(code: Int, reason: String) {
        Log.e("TAG", "onClosed: code: $code, reason: $reason")
        isSocketConnected = false
        reconnectionJob?.cancel()
        uiScope.launch {
            listener?.onClosed(code, reason)
        }
    }

    /**
     * Send message via socket
     */
    override fun send(message: KmeMessage<out KmeMessage.Payload>) {
        try {
            val strMessage = gson.toJson(message)
            Log.e(TAG, "send ==> ${this.hashCode()}  $strMessage")
            webSocket?.send(strMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Disconnect socket connection
     */
    override fun disconnect() {
        Log.e(TAG, "disconnect module: ${this.hashCode()}",)

        if (isSocketConnected) {
            isSocketConnected = false
            allowReconnection = false
            reconnectionJob?.cancel()
            reconnectionJob = null
            webSocket?.cancel()
            webSocket = null
            Log.e(TAG, "disconnect handler: ${webSocketHandler.hashCode()}",)
            webSocketHandler.removeListeners()
        }
    }

    /**
     * Create a web socket url from input data
     */
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

    companion object {
        private val TAG = KmeWebSocketModuleImpl::class.java.canonicalName
        private const val RECONNECTION_ATTEMPTS = 5
    }

}
