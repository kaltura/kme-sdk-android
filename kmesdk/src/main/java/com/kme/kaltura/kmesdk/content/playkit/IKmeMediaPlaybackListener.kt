package com.kme.kaltura.kmesdk.content.playkit

import com.kaltura.playkit.PKEvent

/**
 * An interface for media files playback
 */
interface IKmeMediaPlaybackListener {

    /**
     * Init media view
     *
     * @param config basic config
     * @param onViewInit initiation callback
     */
    fun init(
        config: KmeMediaView.Config,
        onViewInit: () -> Unit
    )

    /**
     * Getting current playing position
     */
    val currentPosition: Long

    /**
     * Getting duration of current media file
     */
    val duration: Long

    /**
     * Start playback
     */
    fun play()

    /**
     * Replay playback
     */
    fun replay()

    /**
     * Pause playback
     */
    fun pause()

    /**
     * Mute/Un-mute audio
     *
     * @param isMute
     */
    fun mute(isMute: Boolean)

    /**
     * Seek to position
     *
     * @param seekTo
     */
    fun seekTo(seekTo: Long)

    /**
     * Check if playback of media file is playing
     *
     * @return 'true' if playback is playing
     */
    fun isPlaying() : Boolean

    /**
     * Check if playback of media file is ended
     *
     * @return 'true' if playback is ended
     */
    fun isEnded(): Boolean

    /**
     * Adding listener for specific event
     *
     * @param E
     * @param groupId
     * @param type
     * @param listener listener for event handling
     */
    fun <E : PKEvent?> addListener(groupId: Any, type: Class<E>, listener: PKEvent.Listener<E>)

    /**
     * Adding event listener
     *
     * @param groupId
     * @param type type of event
     * @param listener listener for event handling
     */
    fun addListener(groupId: Any, type: Enum<*>, listener: PKEvent.Listener<*>)

    /**
     * Removing event listener
     *
     * @param groupId
     */
    fun removeListeners(groupId: Any)

}