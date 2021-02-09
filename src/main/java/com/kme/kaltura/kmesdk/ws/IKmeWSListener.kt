package com.kme.kaltura.kmesdk.ws

import okhttp3.Response

/**
 * An interface for socket connection events
 */
internal interface IKmeWSListener {

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     *
     * @param response
     */
    fun onOpen(response: Response)

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     *
     * @param throwable failure reason
     * @param response
     */
    fun onFailure(throwable: Throwable, response: Response?)

    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     *
     * @param code code of reason
     * @param reason description of a reason
     */
    fun onClosing(code: Int, reason: String)

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     *
     * @param code code of reason
     * @param reason description of a reason
     */
    fun onClosed(code: Int, reason: String)

}
