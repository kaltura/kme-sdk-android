package com.kme.kaltura.kmesdk.ws

interface IKmeWSConnectionListener {

    fun onOpen()

    fun onFailure(throwable: Throwable)

    fun onClosing(code: Int, reason: String)

    fun onClosed(code: Int, reason: String)
}