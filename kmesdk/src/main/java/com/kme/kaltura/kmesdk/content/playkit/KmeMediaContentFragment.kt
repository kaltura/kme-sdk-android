package com.kme.kaltura.kmesdk.content.playkit

import android.os.Bundle
import android.view.KeyEvent
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
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.gone
import com.kme.kaltura.kmesdk.visible
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType
import com.kme.kaltura.kmesdk.ws.message.type.KmePlayerState
import java.util.concurrent.TimeUnit

/**
 * Implementation for media files shared content
 */
class KmeMediaContentFragment : KmeContentView() {

    private val mediaContentViewModel: KmeMediaContentViewModel by scopedInject()

    private var _binding: FragmentMediaContentBinding? = null
    private val binding get() = _binding

    private val payload: SetActiveContentPayload? by lazy { arguments?.getParcelable(CONTENT_PAYLOAD) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaContentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        payload?.let {
            setContentPayload(it)
        }
        setupUI()
        setupKeyEventListener()
    }

    private fun setupUI() = binding?.apply {
        controlsView.setProgressBarVisibility(true)
        controlsView.setControlsVisibility(false)
        controlsView.isClickable = false
        controlsView.setControlsMode(mediaContentViewModel.enabledControls())
        controlsView.controlsEventListener = object : BaseControlsView.OnControlsEventListener {
            override fun onEvent(event: PlayerControlsEvent) {
                handlePlayerState(event)
            }
        }
    }

    private fun handlePlayerState(event: PlayerControlsEvent) = binding?.apply {
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
            else -> {
            }
        }
    }

    private fun subscribePlayerEvents() = binding?.apply {
        mediaView.kalturaErrorListener = object : OnLoadKalturaErrorListener {
            override fun onLoadKalturaMediaError(error: ErrorElement) {
                controlsView.setProgressBarVisibility(false)
                reportPlayerStateChange(KmePlayerState.ENDED)
                Snackbar.make(mediaView, R.string.error_cant_load_media, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        mediaView.addListener(this, PlayerEvent.canPlay) {
            controlsView.setProgressBarVisibility(false)
            reportPlayerStateChange(KmePlayerState.PAUSED)
            // TODO uncomment for moderators
//            controlsView.setControlsVisibility(true)
        }
        mediaView.addListener(this, PlayerEvent.error) {
            controlsView.setProgressBarVisibility(false)
            reportPlayerStateChange(KmePlayerState.ENDED)
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
            reportPlayerStateChange(KmePlayerState.PLAY)
        }
        mediaView.addListener(this, PlayerEvent.playing) {
            controlsView.updateUI(PLAYING)
            reportPlayerStateChange(KmePlayerState.PLAYING)
        }
        mediaView.addListener(this, PlayerEvent.pause) {
            if (mediaView.isYoutube()) {
                controlsView.setSeekBarVisibility(false)
                controlsView.setTimeVisibility(false)
            }
            controlsView.updateUI(PAUSE)
            reportPlayerStateChange(KmePlayerState.PAUSED)
        }
        mediaView.addListener(this, PlayerEvent.stopped) {
            controlsView.updateUI(STOPPED)
            reportPlayerStateChange(KmePlayerState.STOP)
        }
        mediaView.addListener(this, PlayerEvent.ended) {
            controlsView.updateUI(STOPPED)
            reportPlayerStateChange(KmePlayerState.ENDED)
        }
        mediaView.addListener(this, PlayerEvent.playheadUpdated) {
            controlsView.setTimePosition(
                TimeUnit.MILLISECONDS.toSeconds(it.position),
                TimeUnit.MILLISECONDS.toSeconds(it.duration)
            )
        }
    }

    private fun reportPlayerStateChange(state: KmePlayerState) = payload?.contentType?.let {
        mediaContentViewModel.reportPlayerStateChange(state, it)
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
                setContentDefaultImage(contentType)
                binding?.apply {
                    mediaView.lifecycleOwner = viewLifecycleOwner
                    mediaView.init(config) {
                        // Only for KMS case
                        val prevContentType = if (payload?.contentType == KmeContentType.KALTURA) {
                            KmeContentType.KALTURA
                        } else {
                            it
                        }
                        payload?.contentType = it
                        mediaContentViewModel.getPlayerState(prevContentType)
                        subscribePlayerEvents()
                    }
                    mediaView.mute(mediaContentViewModel.isMute)
                }
            }
        }
    }

    private fun setContentDefaultImage(contentType: KmeContentType) {
        binding?.contentDefaultImageView.visible()
        when (contentType) {
            KmeContentType.AUDIO -> {
                binding?.contentDefaultImageView?.setImageResource(R.drawable.ic_sound_file)
            }
            else -> {
                binding?.contentDefaultImageView.gone()
            }
        }
    }

    private fun setupKeyEventListener() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    mediaContentViewModel.videoVolumeIncrease()
                    true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    mediaContentViewModel.videoVolumeDecrease()
                    true
                }
                else -> false
            }
        }
    }

    fun mute(isMute: Boolean) {
        mediaContentViewModel.isMute = isMute
        binding?.mediaView?.mute(isMute)
    }

    override fun onDestroyView() {
        binding?.apply {
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
