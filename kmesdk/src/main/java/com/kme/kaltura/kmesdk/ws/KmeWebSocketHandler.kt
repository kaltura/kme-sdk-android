package com.kme.kaltura.kmesdk.ws

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.logger.IKmeLogger
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
    private val messageManager: KmeMessageManager,
    private val webSocketType: KmeWebSocketType,
    private val logger: IKmeLogger
) : WebSocketListener(), IKmeMessageManager {

    private val TAG = KmeWebSocketHandler::class.simpleName.toString()

    private val pingValue = 2
    private val pongValue = 3

    var listener: IKmeWSListener? = null

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        logger.d(TAG, "onOpen ${webSocket.hashCode()}: $response")
        listener?.onOpen(response)
    }

    /** Invoked when a text (type `0x1`) message has been received. */
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        //FIXME UnknownFormatConversionException
        // text = {"constraint":["INTERNAL_FORWARD","INCLUDE_SELF"],"name":"setActiveContent",
        // "module":"activecontent","type":"BROADCAST",
        // "payload":{"controllingUser":33808585, "content_type":"slides",
        // "metadata":{"playlistFileId":null,"progress":null,"caller":"playlist",
        // "activeItem":"529073","file_name":"giphy (1).gif","file_id":158825,"file_type":"gif",
        // "current_slide":1,"active_clip":null,"play_state":null,
        // "slides":[{"slide_number":1,"audio_clips":null,
        // "url":"https://stg-ctn-cf.newrow.com/companyFiles/39247/1765105/1649669144_260199709/37312466_1649669144.gif",
        // "thumbnail":"https://stg-ctn-cf.newrow.com/companyFiles/39247/1765105/1649669144_260199709/converted%2F37312466_1649669144-thumbnail.jpg"}]}}}
        // logger.e(TAG, "${webSocket.hashCode()} onMessage: <== $text")

        logger.d(TAG, "${webSocket.hashCode()} onMessage: <== $text")
        if (text.isNotEmpty()) {
            handleMessage(webSocket, text)
        }
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        logger.d(TAG, "onFailure ${webSocket.hashCode()}: $t, $response")
        listener?.onFailure(t, response)
    }

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        logger.d(TAG, "onClosing ${webSocket.hashCode()}: $code, $reason")
        listener?.onClosing(code, reason)
    }

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        logger.d(TAG, "onClosed ${webSocket.hashCode()}: $code, $reason")
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
                    messageManager.post(webSocketType, event, message)
                }
            }
        }
    }

    override fun addListener(
        listener: IKmeMessageListener,
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ) {
        messageManager.addListener(listener, priority, filter)
    }

    override fun addListener(
        event: KmeMessageEvent,
        listener: IKmeMessageListener,
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ) {
        messageManager.addListener(event, listener, priority, filter)
    }

    override fun listen(
        listener: IKmeMessageListener,
        vararg events: KmeMessageEvent,
        priority: KmeMessagePriority,
        filter: KmeMessageFilter
    ): IKmeMessageListener {
        return messageManager.listen(listener, *events, priority = priority, filter = filter)
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
