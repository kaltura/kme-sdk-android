package com.kme.kaltura.kmesdk.content.playkit

import com.kaltura.playkit.PKEvent

interface IKmeMediaPlaybackListener {

    fun init(config: KmeMediaView.Config)

    fun setMedia(url: String)

    val currentPosition: Long

    val duration: Long

    fun play()

    fun replay()

    fun pause()

    fun seekTo(seekTo: Long)

    fun isEnded(): Boolean

    fun <E : PKEvent?> addListener(groupId: Any, type: Class<E>, listener: PKEvent.Listener<E>)

    fun addListener(groupId: Any, type: Enum<*>, listener: PKEvent.Listener<*>)

    fun removeListeners(groupId: Any)

}