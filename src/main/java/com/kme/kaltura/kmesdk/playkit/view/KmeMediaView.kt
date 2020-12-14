package com.kme.kaltura.kmesdk.playkit.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.kaltura.playkit.*
import com.kaltura.playkit.player.PKHttpClientManager
import com.kaltura.tvplayer.KalturaPlayer
import com.kaltura.tvplayer.PlayerInitOptions
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.playkit.IKmeMediaPlaybackListener
import com.kme.kaltura.kmesdk.playkit.asKalturaEvent
import com.kme.kaltura.kmesdk.playkit.isEnded
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.concurrent.TimeUnit

class KmeMediaView : FrameLayout, IKmeMediaPlaybackListener {

    private lateinit var config: Config
    private var kalturaPlayer: KalturaPlayer? = null

    private var messageBus: MessageBus? = null
    private var youtubePlayer: YouTubePlayer? = null
    private var youtubePlayerView: YouTubePlayerView? = null
    private var youtubeTracker: YouTubePlayerTracker? = null
    private var youtubePlayerListener: AbstractYouTubePlayerListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun init(config: Config) {
        removeAllViews()

        this.config = config

        if (isYoutube()) {
            releaseKalturaPlayer()
            setupYoutubePlayer()
            setupYoutubePlayerView()
        } else {
            releaseYoutubePlayer()
            setupKalturaPlayer()
            setupKalturaPlayerView()
        }
    }

    override fun setMedia(url: String) {
        check(::config.isInitialized) {
            "${javaClass.simpleName} is not initialized."
        }

        setupMedia(url)
    }

    private fun setupYoutubePlayer() {
        messageBus = MessageBus()
        youtubePlayerView = YouTubePlayerView(context).apply {
            getPlayerUiController().showUi(false)
            getPlayerUiController().showMenuButton(false)
            getPlayerUiController().showYouTubeButton(false)
        }
    }

    private fun setupYoutubePlayerView() {
        addView(youtubePlayerView)
    }

    private fun setupKalturaPlayer() {
        PKHttpClientManager.setHttpProvider(HTTP_PROVIDER_ID)

        val playerInitOptions = PlayerInitOptions()
        playerInitOptions.autoplay = config.autoPlay
        playerInitOptions.useTextureView = true

        playerInitOptions.contentRequestAdapter = object : PKRequestParams.Adapter {
            override fun adapt(requestParams: PKRequestParams): PKRequestParams {
                requestParams.headers["Cookie"] = config.cookie
                return requestParams
            }

            override fun updateParams(player: Player?) {
            }

            override fun getApplicationName(): String {
                return context.getString(R.string.app_name)
            }
        }

        kalturaPlayer = KalturaPlayer.createBasicPlayer(context, playerInitOptions)
    }

    private fun setupKalturaPlayerView() {
        kalturaPlayer?.setPlayerView(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(kalturaPlayer?.playerView)
    }

    private fun setupMedia(url: String) {
        if (isYoutube()) {
            subscribeToYoutubeEvents(url)
        } else {
            val media = createMediaEntry(url)
            kalturaPlayer?.setMedia(media)
        }
    }

    private fun subscribeToYoutubeEvents(url: String) {
        youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                youtubePlayer = youTubePlayer

                youtubeTracker = YouTubePlayerTracker().also {
                    youtubePlayer?.addListener(it)
                }

                youtubePlayer?.cueVideo(url, 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                state.asKalturaEvent()?.let {
                    messageBus?.post(it)
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                val event = PlayerEvent.PlayheadUpdated(
                    TimeUnit.SECONDS.toMillis(second.toLong()),
                    TimeUnit.SECONDS.toMillis(duration)
                )
                messageBus?.post(event)
            }
        }.also {
            youtubePlayerView?.addYouTubePlayerListener(it)
        }
    }

    private fun createMediaEntry(url: String): PKMediaEntry {
        return PKMediaEntry().apply {
            id = ENTRY_ID
            mediaType = PKMediaEntry.MediaEntryType.Vod
            sources = createMediaSources(url)
        }
    }

    private fun createMediaSources(url: String): List<PKMediaSource> {
        val mediaSource = PKMediaSource().apply {
            id = MEDIA_SOURCE_ID
            this.url = url
            mediaFormat = PKMediaFormat.valueOfUrl(url)
        }
        return listOf(mediaSource)
    }

    override val currentPosition: Long
        get() = if (isYoutube())
            youtubeTracker?.currentSecond?.toLong() ?: 0L
        else
            TimeUnit.MILLISECONDS.toSeconds(kalturaPlayer?.currentPosition ?: 0L)

    override val duration: Long
        get() = if (isYoutube())
            youtubeTracker?.videoDuration?.toLong() ?: 0L
        else
            TimeUnit.MILLISECONDS.toSeconds(kalturaPlayer?.duration ?: 0L)

    override fun play() {
        if (isYoutube()) {
            youtubePlayer?.play()
            messageBus?.post(PlayerEvent.Generic(PlayerEvent.Type.PLAY))
        } else {
            kalturaPlayer?.play()
        }
    }

    override fun replay() {
        if (isYoutube()) {
            youtubePlayer?.seekTo(0f)
            youtubePlayer?.play()
            messageBus?.post(PlayerEvent.Generic(PlayerEvent.Type.PLAY))
        } else {
            kalturaPlayer?.replay()
        }
    }

    override fun pause() {
        if (isYoutube()) {
            youtubePlayer?.pause()
            messageBus?.post(PlayerEvent.Generic(PlayerEvent.Type.PAUSE))
        } else {
            kalturaPlayer?.pause()
        }
    }

    override fun seekTo(seekTo: Long) {
        val seekToMillis = TimeUnit.SECONDS.toMillis(seekTo)
        if (isYoutube()) {
            youtubePlayer?.seekTo(seekTo.toFloat())
            messageBus?.post(PlayerEvent.Seeking(seekToMillis))
        } else {
            kalturaPlayer?.seekTo(seekToMillis)
        }
    }

    override fun isEnded(): Boolean {
        return if (isYoutube()) {
            youtubeTracker?.state == PlayerConstants.PlayerState.ENDED
        } else {
            kalturaPlayer?.isEnded() ?: true
        }
    }

    override fun <E : PKEvent?> addListener(
        groupId: Any,
        type: Class<E>,
        listener: PKEvent.Listener<E>
    ) {
        if (isYoutube()) {
            messageBus?.addListener(groupId, type, listener)
        } else {
            kalturaPlayer?.addListener(groupId, type, listener)
        }
    }

    override fun addListener(groupId: Any, type: Enum<*>, listener: PKEvent.Listener<*>) {
        if (isYoutube()) {
            messageBus?.addListener(groupId, type, listener)
        } else {
            kalturaPlayer?.addListener(groupId, type, listener)
        }
    }

    override fun removeListeners(groupId: Any) {
        messageBus?.removeListeners(groupId)
        kalturaPlayer?.removeListeners(groupId)
    }

    fun isYoutube() = config.contentType == KmeContentType.YOUTUBE

    fun release() {
        removeAllViews()
        releaseKalturaPlayer()
        releaseYoutubePlayer()
    }

    private fun releaseKalturaPlayer() {
        kalturaPlayer?.destroy()
        kalturaPlayer = null
    }

    private fun releaseYoutubePlayer() {
        youtubeTracker?.let {
            youtubePlayer?.removeListener(it)
        }
        youtubeTracker = null

        youtubePlayerListener?.let {
            youtubePlayerView?.removeYouTubePlayerListener(it)
        }
        youtubePlayerListener = null
        youtubePlayerView = null
        youtubePlayer = null
        messageBus = null
    }

    class Config(
        val contentType: KmeContentType,
        val cookie: String,
    ) {
        var autoPlay: Boolean = true
    }

    companion object {
        const val HTTP_PROVIDER_ID = "okhttp"
        const val ENTRY_ID = "shared_media_content_id"
        const val MEDIA_SOURCE_ID = "shared_media_source_id"
    }

}