package com.kme.kaltura.kmesdk.content.playkit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.kaltura.netkit.utils.ErrorElement
import com.kaltura.playkit.PlayerEvent
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.content.playkit.controls.BaseControlsView
import com.kme.kaltura.kmesdk.content.playkit.controls.PlayerControlsEvent
import com.kme.kaltura.kmesdk.content.playkit.controls.PlayerControlsEvent.*
import com.kme.kaltura.kmesdk.databinding.FragmentMediaContentBinding
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class KmeMediaContentFragment : KmeContentView() {

    private val mediaContentViewModel: KmeMediaContentViewModel by inject()

    private var _binding: FragmentMediaContentBinding? = null
    private val binding get() = _binding!!

    private val payload: SetActiveContentPayload? by lazy { arguments?.getParcelable(CONTENT_PAYLOAD) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaContentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        payload?.let {
            setContentPayload(it)
        }
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            controlsView.setProgressBarVisibility(true)
            controlsView.setControlsVisibility(false)
            controlsView.setControlsMode(mediaContentViewModel.enabledControls())
            controlsView.controlsEventListener = object : BaseControlsView.OnControlsEventListener {
                override fun onEvent(event: PlayerControlsEvent) {
                    handlePlayerState(event)
                }
            }
        }
    }

    private fun handlePlayerState(event: PlayerControlsEvent) {
        when (event) {
            PLAY -> {
                with(binding) {
                    if (mediaView.isEnded()) {
                        mediaView.replay()
                    } else {
                        mediaView.play()
                    }
                }
            }
            PAUSE -> {
                binding.mediaView.pause()
            }
            else -> {
            }
        }
    }

    private fun subscribePlayerEvents() {
        with(binding) {
            mediaView.kalturaErrorListener = object : OnLoadKalturaErrorListener {
                override fun onLoadKalturaMediaError(error: ErrorElement) {
                    controlsView.setProgressBarVisibility(false)
                    Snackbar.make(mediaView, R.string.error_cant_load_media, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
            mediaView.addListener(this, PlayerEvent.canPlay) {
                controlsView.setProgressBarVisibility(false)
                controlsView.setControlsVisibility(true)
            }
            mediaView.addListener(this, PlayerEvent.error) {
                controlsView.setProgressBarVisibility(false)
                Snackbar.make(mediaView, R.string.error_cant_load_media, Snackbar.LENGTH_SHORT)
                    .show()
            }
            mediaView.addListener(this, PlayerEvent.seeking) {
                controlsView.setTimePosition(
                    TimeUnit.MILLISECONDS.toSeconds(it.targetPosition),
                    mediaView.duration
                )
            }
            mediaView.addListener(this, PlayerEvent.play) {
                controlsView.setSeekBarVisibility(true)
                controlsView.setTimeVisibility(true)
                controlsView.updateUI(PLAY)
            }
            mediaView.addListener(this, PlayerEvent.playing) {
                controlsView.updateUI(PLAYING)
            }
            mediaView.addListener(this, PlayerEvent.pause) {
                if (mediaView.isYoutube()) {
                    controlsView.setSeekBarVisibility(false)
                    controlsView.setTimeVisibility(false)
                }
                controlsView.updateUI(PAUSE)
            }
            mediaView.addListener(this, PlayerEvent.stopped) {
                controlsView.updateUI(STOPPED)
            }
            mediaView.addListener(this, PlayerEvent.ended) {
                controlsView.updateUI(STOPPED)
            }
            mediaView.addListener(this, PlayerEvent.playheadUpdated) {
                controlsView.setTimePosition(
                    TimeUnit.MILLISECONDS.toSeconds(it.position),
                    TimeUnit.MILLISECONDS.toSeconds(it.duration)
                )
            }
        }
    }

    private fun setContentPayload(contentPayload: SetActiveContentPayload) {
        contentPayload.metadata.let { metadata ->
            val contentType = contentPayload.contentType

            if (contentType != null) {
                val config = KmeMediaView.Config(
                    contentType,
                    metadata,
                    mediaContentViewModel.getCookie()
                ).apply {
                    partnerId = mediaContentViewModel.getKalturaPartnerId()
                    autoPlay = false
                }

                binding.mediaView.init(config)
                subscribePlayerEvents()
            }
        }
    }

    override fun onDestroyView() {
        with(binding) {
            mediaView.removeListeners(this)
            mediaView.release()
        }
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CONTENT_PAYLOAD = "CONTENT_PAYLOAD"

        @JvmStatic
        fun newInstance(
            payload: SetActiveContentPayload
        ) = KmeMediaContentFragment().apply {
            arguments = Bundle().apply {
                putParcelable(CONTENT_PAYLOAD, payload)
            }
        }
    }

}
