package com.kme.kaltura.kmesdk.ws

import okhttp3.Response

internal interface IKmeWSListener {

    fun onOpen(response: Response)

    fun onFailure(throwable: Throwable, response: Response?)

    fun onClosing(code: Int, reason: String)

    fun onClosed(code: Int, reason: String)

}