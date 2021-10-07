package com.kme.kaltura.kmesdk.ws

import android.util.Log
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Wrapper under OkHttp socket implementation
 *
 * @property messageParser an implementation for incoming messages parser
 * @property messageManager an implementation transferring messages to appropriate listeners
 */
internal class KmeWebSocketHandler(
    private val messageParser: KmeMessageParser,
    private val messageManager: KmeMessageManager
) : WebSocketListener(), IKmeMessageManager {

    private val TAG = KmeWebSocketHandler::class.java.canonicalName

    private val pingValue = 2
    private val pongValue = 3

    var listener: IKmeWSListener? = null

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.e(TAG, "onOpen ${webSocket.hashCode()}: $response")
        listener?.onOpen(response)
    }

    /** Invoked when a text (type `0x1`) message has been received. */
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.e(TAG, "${hashCode()} onMessage: $text")
        handleMessage(webSocket, text)
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e(TAG, "onFailure ${webSocket.hashCode()}: $t, $response")
        listener?.onFailure(t, response)
    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.e(TAG, "onClosing ${webSocket.hashCode()}: $code, $reason")
        listener?.onClosing(code, reason)
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.e(TAG, "onClosed ${webSocket.hashCode()}: $code, $reason")
        listener?.onClosed(code, reason)
    }

    /**
     * Handle incoming message. Do a 'Pong' in case 'Ping' received. Otherwise parse message and
     * transfer to the application listeners
     *
     * @param webSocket
     * @param text
     */
    private fun handleMessage(webSocket: WebSocket, text: String) {
        if (isPingMessage(text)) {
            webSocket.send(pongValue.toString())
        } else {
            post(text)
        }
    }

    /**
     * Checks ping message
     *
     * @param text incoming message
     */
    private fun isPingMessage(text: String) = pingValue.toString() == text

    /**
     * Transfer messages to appropriate listeners
     *
     * @param text incoming message
     */
    private fun post(text: String) {
        val message = messageParser.parse(text)
        message?.let {
            val events = it.payload?.events
            if (!events.isNullOrEmpty()) {
                for (json in events) {
                    post(json)
                }
            } else {
                val event = it.name
                if (event != null) {
                    messageManager.post(event, message)
                }
            }
        }
    }

    override fun addListener(listener: IKmeMessageListener) {
        messageManager.addListener(listener)
    }

    override fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener
    ) {
        messageManager.addListener(event, listener)
    }

    override fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
    ): IKmeMessageListener {
        return messageManager.listen(listener, *events)
    }

    override fun remove(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent
    ) {
        messageManager.removeListeners()
    }

    override fun removeListener(listener: IKmeMessageListener) {
        messageManager.removeListeners()
    }

    override fun removeListeners() {
        messageManager.removeListeners()
    }

}
