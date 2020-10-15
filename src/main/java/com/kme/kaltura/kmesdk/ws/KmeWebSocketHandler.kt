package com.kme.kaltura.kmesdk.ws

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

internal class KmeWebSocketHandler(
    private val messageParser: KmeMessageParser,
    private val messageManager: KmeMessageManager
) : WebSocketListener() {

    private val TAG = KmeWebSocketHandler::class.java.canonicalName

    private val pingValue = 2
    private val pongValue = 3

    var listener: IKmeWSListener? = null

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.e(TAG, "onOpen: $response")
        listener?.onOpen(response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.e(TAG, "onMessage: $text")
        handleMessage(webSocket, text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e(TAG, "onFailure: $t, $response")
        listener?.onFailure(t, response)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.e(TAG, "onClosing: $code, $reason")
        listener?.onClosing(code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.e(TAG, "onClosed: $code, $reason")
        listener?.onClosed(code, reason)
    }

    private fun handleMessage(webSocket: WebSocket, text: String) {
        if (isPingMessage(text)) {
            webSocket.send(pongValue.toString())
        } else {
            post(text)
        }
    }

    private fun isPingMessage(text: String) = pingValue.toString() == text

    private fun post(text: String) {
        val message = messageParser.parse(text)
        message?.let {
            val event = it.name
            if (event != null) {
                messageManager.post(event, message)
            }
        }
    }

}
