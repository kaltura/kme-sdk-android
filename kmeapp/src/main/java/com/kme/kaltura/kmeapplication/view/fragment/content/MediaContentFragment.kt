package com.kme.kaltura.kmeapplication.view.fragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kaltura.playkit.PlayerEvent
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.view.view.content.controls.BaseControlsView
import com.kme.kaltura.kmeapplication.view.view.content.controls.PlayerControlsEvent
import com.kme.kaltura.kmeapplication.view.view.content.controls.PlayerControlsEvent.*
import com.kme.kaltura.kmeapplication.viewmodel.content.ActiveContentViewModel
import com.kme.kaltura.kmesdk.content.playkit.KmeMediaView
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import kotlinx.android.synthetic.main.fragment_media_content.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class MediaContentFragment : Fragment() {

    private val activeContentViewModel: ActiveContentViewModel by sharedViewModel()

    private var syncPlayerEvent: PlayerControlsEvent? = null
    private var syncPlayerPosition: Float = 0f

    private var canPlay: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        activeContentViewModel.setActiveContentLiveData.observe(
            viewLifecycleOwner,
            setActiveContentObserver
        )
        activeContentViewModel.syncPlayerStateLiveData.observe(
            viewLifecycleOwner,
            syncPlayerStateObserver
        )
    }

    private fun setupUI() {
        controlsView?.setProgressBarVisibility(true)
        controlsView?.setControlsVisibility(false)
        controlsView?.setControlsMode(activeContentViewModel.enabledControls())
        controlsView?.controlsEventListener = object : BaseControlsView.OnControlsEventListener {
            override fun onEvent(event: PlayerControlsEvent) {
                handlePlayerState(event)
            }
        }
    }

    private fun handlePlayerState(event: PlayerControlsEvent) {
        when (event) {
            PLAY -> {
                if (mediaView.isEnded()) {
                    mediaView.replay()
                } else {
                    mediaView.play()
                }
            }
            PAUSE -> {
                mediaView.pause()
            }
        }
    }

    private fun subscribePlayerEvents() {
        mediaView.addListener(this, PlayerEvent.canPlay) {
            canPlay = true
            syncPlayerState()
            controlsView?.setProgressBarVisibility(false)
            controlsView?.setControlsVisibility(true)
        }
        mediaView.addListener(this, PlayerEvent.error) {
            controlsView?.setProgressBarVisibility(false)
        }
        mediaView.addListener(this, PlayerEvent.seeking) {
            controlsView?.setTimePosition(
                TimeUnit.MILLISECONDS.toSeconds(it.targetPosition),
                mediaView.duration
            )
        }
        mediaView.addListener(this, PlayerEvent.play) {
            controlsView?.setSeekBarVisibility(true)
            controlsView?.setTimeVisibility(true)
            controlsView?.updateUI(PLAY)
        }
        mediaView.addListener(this, PlayerEvent.playing) {
            controlsView?.updateUI(PLAYING)
        }
        mediaView.addListener(this, PlayerEvent.pause) {
            if (mediaView.isYoutube()) {
                controlsView?.setSeekBarVisibility(false)
                controlsView?.setTimeVisibility(false)
            }
            controlsView?.updateUI(PAUSE)
        }
        mediaView.addListener(this, PlayerEvent.stopped) {
            controlsView?.updateUI(STOPPED)
        }
        mediaView.addListener(this, PlayerEvent.ended) {
            controlsView?.updateUI(STOPPED)
        }
        mediaView.addListener(this, PlayerEvent.playheadUpdated) {
            controlsView?.setTimePosition(
                TimeUnit.MILLISECONDS.toSeconds(it.position),
                TimeUnit.MILLISECONDS.toSeconds(it.duration)
            )
        }
    }

    private fun setMedia(url: String) {
        canPlay = false
        mediaView.setMedia(url)
    }

    private val setActiveContentObserver = Observer<SetActiveContentPayload> {
        it.metadata.let { metadata ->
            val contentType = it.contentType
            val contentUrl: String? =
                if (contentType == KmeContentType.YOUTUBE)
                    metadata.fileId
                else
                    metadata.src

            if (contentType != null && contentUrl != null) {
                val config = KmeMediaView.Config(contentType, activeContentViewModel.getCookie())
                    .apply {
                        autoPlay = false
                    }

                mediaView.init(config)
                subscribePlayerEvents()
                setMedia(contentUrl)
            }
        }
    }

    private val syncPlayerStateObserver = Observer<Pair<PlayerControlsEvent?, Float>> {
        syncPlayerEvent = it.first
        syncPlayerPosition = it.second
        syncPlayerState()
    }

    private fun syncPlayerState() {
        if (canPlay) {
            mediaView.seekTo(syncPlayerPosition.toLong())
            syncPlayerEvent?.let {
                handlePlayerState(it)
            }
        }
    }

    override fun onDestroyView() {
        mediaView.removeListeners(this)
        mediaView.release()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MediaContentFragment()
    }
}