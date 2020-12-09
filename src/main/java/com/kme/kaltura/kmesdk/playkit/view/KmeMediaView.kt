package com.kme.kaltura.kmesdk.playkit.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.kaltura.playkit.*
import com.kaltura.playkit.player.PKHttpClientManager
import com.kaltura.tvplayer.KalturaPlayer
import com.kaltura.tvplayer.PlayerInitOptions
import com.kme.kaltura.kmesdk.R

class KmeMediaView : FrameLayout {

    lateinit var player: KalturaPlayer

    private lateinit var config: Config

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init(config: Config) {
        this.config = config

        createPlayer()
        setupPlayerView()
    }

    fun setMedia(url: String) {
        check(::config.isInitialized) {
            "${javaClass.simpleName} is not initialized."
        }

        setupMedia(url)
    }

    private fun createPlayer() {
        PKHttpClientManager.setHttpProvider(HTTP_PROVIDER_ID)

        val playerInitOptions = PlayerInitOptions()
        playerInitOptions.autoplay = config.autoPlay

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

        player = KalturaPlayer.createBasicPlayer(context, playerInitOptions)
    }

    private fun setupPlayerView() {
        player.setPlayerView(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(player.playerView)
    }

    private fun setupMedia(url: String) {
        val media = createMediaEntry(url)
        player.setMedia(media)
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

    class Config(
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