package com.kme.kaltura.kmesdk.content.playkit

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.kaltura.playkit.*
import com.kaltura.playkit.player.PKHttpClientManager
import com.kaltura.tvplayer.KalturaPlayer
import com.kaltura.tvplayer.OVPMediaOptions
import com.kaltura.tvplayer.PlayerInitOptions
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.core.inject
import java.util.concurrent.TimeUnit

/**
 * An implementation of view for media files playback
 */
class KmeMediaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    KmeKoinComponent, IKmeMediaPlaybackListener {

    var kalturaErrorListener: OnLoadKalturaErrorListener? = null

    private val defaultPlayerEventHandler: KmeDefaultPlayerEventHandler by inject()

    private lateinit var config: Config

    private var kalturaPlayer: KalturaPlayer? = null
    private var messageBus: MessageBus? = null
    private var youtubePlayer: YouTubePlayer? = null
    private var youtubePlayerView: YouTubePlayerView? = null
    private var youtubeTracker: YouTubePlayerTracker? = null
    private var youtubePlayerListener: AbstractYouTubePlayerListener? = null

    private var syncPlayerState: KmePlayerState? = null
    private var syncPlayerPosition: Float = 0f
    private var canPlay: Boolean = false

    /**
     * Init media view
     */
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

        if (config.useDefaultHandler) {
            setupDefaultPlayerEventHandler()
        }

        setupMedia()
    }

    private fun setupDefaultPlayerEventHandler() {
        addListener(this, PlayerEvent.canPlay) {
            canPlay = true
            syncPlayerState()
        }
        defaultPlayerEventHandler.syncPlayerStateLiveData.observeForever(syncPlayerStateObserver)
        defaultPlayerEventHandler.subscribe()
    }

    private fun setupYoutubePlayer() {
        messageBus = MessageBus()
        youtubePlayerView = YouTubePlayerView(context).apply {
            enableBackgroundPlayback(false)
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

        val playerInitOptions = if (config.partnerId == 0)
            PlayerInitOptions()
        else
            PlayerInitOptions(config.partnerId)

        playerInitOptions.apply {
            autoplay = config.autoPlay
            useTextureView = true
            contentRequestAdapter = object : PKRequestParams.Adapter {
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
        }

        kalturaPlayer = if (config.contentType == KmeContentType.KALTURA) {
            KalturaPlayer.initializeOVP(
                context,
                config.partnerId,
                KalturaPlayer.DEFAULT_OVP_SERVER_URL
            )
            KalturaPlayer.createOVPPlayer(context, playerInitOptions)
        } else {
            KalturaPlayer.createBasicPlayer(context, playerInitOptions)
        }
    }

    private fun setupKalturaPlayerView() {
        kalturaPlayer?.setPlayerView(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(kalturaPlayer?.playerView)
    }

    private fun setupMedia() {
        canPlay = false

        if (isYoutube()) {
            subscribeToYoutubeEvents()
        } else {
            loadKalturaMedia()
        }
    }

    private fun subscribeToYoutubeEvents() {
        val url = config.metadata.fileId ?: return

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

    private fun loadKalturaMedia() {
        val entryId = config.metadata.entryId
        if (entryId.isNullOrEmpty()) {
            kalturaPlayer?.setMedia(createMediaEntry())
        } else {
            val ovpMediaOptions = buildOvpMediaOptions()
            kalturaPlayer?.loadMedia(ovpMediaOptions) { entry, loadError ->
                if (loadError != null) {
                    kalturaErrorListener?.onLoadKalturaMediaError(loadError)
                }
            }
        }
    }

    private fun createMediaEntry(): PKMediaEntry {
        return PKMediaEntry().apply {
            this.id = config.metadata.entryId ?: ENTRY_ID
            this.mediaType = PKMediaEntry.MediaEntryType.Vod
            this.sources = createMediaSources()
        }
    }

    private fun createMediaSources(): List<PKMediaSource>? {
        val url = config.metadata.src ?: return null

        val mediaSource = PKMediaSource().apply {
            id = MEDIA_SOURCE_ID
            this.url = url
            mediaFormat = PKMediaFormat.valueOfUrl(url)
        }
        return listOf(mediaSource)
    }

    private fun buildOvpMediaOptions(): OVPMediaOptions {
        val ovpMediaOptions = OVPMediaOptions()
        ovpMediaOptions.entryId = config.metadata.entryId
        ovpMediaOptions.ks = config.metadata.ks

        return ovpMediaOptions
    }

    private fun syncPlayerState() {
        if (canPlay) {
            seekTo(syncPlayerPosition.toLong())
            syncPlayerState?.let {
                handlePlayerState(it)
            }
        }
    }

    private fun handlePlayerState(state: KmePlayerState) {
        when (state) {
            KmePlayerState.PLAY -> {
                if (isEnded()) {
                    replay()
                } else {
                    play()
                }
            }
            KmePlayerState.PAUSE -> {
                pause()
            }
        }
    }

    /**
     * Getting current playing position
     */
    override val currentPosition: Long
        get() = if (isYoutube())
            youtubeTracker?.currentSecond?.toLong() ?: 0L
        else
            TimeUnit.MILLISECONDS.toSeconds(kalturaPlayer?.currentPosition ?: 0L)

    /**
     * Getting duration of current media file
     */
    override val duration: Long
        get() = if (isYoutube())
            youtubeTracker?.videoDuration?.toLong() ?: 0L
        else
            TimeUnit.MILLISECONDS.toSeconds(kalturaPlayer?.duration ?: 0L)

    /**
     * Start playback
     */
    override fun play() {
        if (isYoutube()) {
            youtubePlayer?.play()
            messageBus?.post(PlayerEvent.Generic(PlayerEvent.Type.PLAY))
        } else {
            kalturaPlayer?.play()
        }
    }

    /**
     * Replay playback
     */
    override fun replay() {
        if (isYoutube()) {
            youtubePlayer?.seekTo(0f)
            youtubePlayer?.play()
            messageBus?.post(PlayerEvent.Generic(PlayerEvent.Type.PLAY))
        } else {
            kalturaPlayer?.replay()
        }
    }

    /**
     * Pause playback
     */
    override fun pause() {
        if (isYoutube()) {
            youtubePlayer?.pause()
            messageBus?.post(PlayerEvent.Generic(PlayerEvent.Type.PAUSE))
        } else {
            kalturaPlayer?.pause()
        }
    }

    /**
     * Seek to position
     */
    override fun seekTo(seekTo: Long) {
        val seekToMillis = TimeUnit.SECONDS.toMillis(seekTo)
        if (isYoutube()) {
            youtubePlayer?.seekTo(seekTo.toFloat())
            messageBus?.post(PlayerEvent.Seeking(seekToMillis))
        } else {
            kalturaPlayer?.seekTo(seekToMillis)
        }
    }

    /**
     * Check is playback of media file is ended
     */
    override fun isEnded(): Boolean {
        return if (isYoutube()) {
            youtubeTracker?.state == PlayerConstants.PlayerState.ENDED
        } else {
            kalturaPlayer?.isEnded() ?: true
        }
    }

    private val syncPlayerStateObserver = Observer<Pair<KmePlayerState?, Float>> {
        syncPlayerState = it.first
        syncPlayerPosition = it.second
        syncPlayerState()
    }

    /**
     * Adding listener for specific event
     */
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

    /**
     * Adding event listener
     */
    override fun addListener(groupId: Any, type: Enum<*>, listener: PKEvent.Listener<*>) {
        if (isYoutube()) {
            messageBus?.addListener(groupId, type, listener)
        } else {
            kalturaPlayer?.addListener(groupId, type, listener)
        }
    }

    /**
     * Removing event listener
     */
    override fun removeListeners(groupId: Any) {
        messageBus?.removeListeners(groupId)
        kalturaPlayer?.removeListeners(groupId)
    }

    fun isYoutube() = config.contentType == KmeContentType.YOUTUBE

    /*
    * Releases all available player instances. Removes all player views.
    * Unsubscribe from player events.
    * */
    fun release() {
        removeAllViews()
        releaseKalturaPlayer()
        releaseYoutubePlayer()
        removeListeners(this)
        if (config.useDefaultHandler) {
            defaultPlayerEventHandler.release()
        }
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
        val metadata: KmeActiveContentModuleMessage.ActiveContentPayload.Metadata,
        val cookie: String,
    ) {
        var partnerId: Int = 0
        var autoPlay: Boolean = true
        var useDefaultHandler = true
    }

    companion object {
        const val HTTP_PROVIDER_ID = "okhttp"
        const val ENTRY_ID = "shared_media_content_id"
        const val MEDIA_SOURCE_ID = "shared_media_source_id"
    }

}