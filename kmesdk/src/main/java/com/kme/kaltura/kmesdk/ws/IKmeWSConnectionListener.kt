package com.kme.kaltura.kmesdk.ws

/**
 * An interface for socket connection events
 */
interface IKmeWSConnectionListener {

    /**
     * Event about socket connection opened
     */
    fun onOpen()

    /**
     * Event about socket failure
     *
     * @param throwable failure reason
     */
    fun onFailure(throwable: Throwable)

    /**
     * Event about socket connection closing
     *
     * @param code code of reason
     * @param reason description of a reason
     */
    fun onClosing(code: Int, reason: String)

    /**
     * Event about socket connection closed
     *
     * @param code code of reason
     * @param reason description of a reason
     */
    fun onClosed(code: Int, reason: String)

}
