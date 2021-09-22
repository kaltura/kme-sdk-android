package com.kme.kaltura.kmeapplication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kme.kaltura.kmeapplication.GridSpacingItemDecoration
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.view.adapter.RenderersAdapter
import com.kme.kaltura.kmeapplication.viewmodel.ParticipantsViewModel
import com.kme.kaltura.kmeapplication.viewmodel.PeerConnectionViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomRenderersViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomSettingsViewModel
import com.kme.kaltura.kmesdk.ws.message.module.KmeParticipantsModuleMessage.UserMediaStateChangedPayload
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaStateType
import kotlinx.android.synthetic.main.fragment_renderers.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RoomRenderersFragment : KmeFragment() {

    private val viewModel: RoomRenderersViewModel by viewModel()
    private val participantsViewModel: ParticipantsViewModel by sharedViewModel()
    private val roomSettingsViewModel: RoomSettingsViewModel by sharedViewModel()
    private val peerConnectionViewModel: PeerConnectionViewModel by sharedViewModel()

    private lateinit var adapter: RenderersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_renderers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        participantsViewModel.participantMediaStateChangedLiveData.observe(
            viewLifecycleOwner,
            mediaStateChangedObserver
        )
        participantsViewModel.allMicsToggledByAdminLiveData.observe(
            viewLifecycleOwner,
            micToggleByAdminObserver
        )
        participantsViewModel.allCamsToggledByAdminLiveData.observe(
            viewLifecycleOwner,
            camToggleByAdminObserver
        )
        participantsViewModel.participantHandRaisedLiveData.observe(
            viewLifecycleOwner,
            raiseHandObserver
        )
        participantsViewModel.allHandsDownByAdminLiveData.observe(
            viewLifecycleOwner,
            allHandsDownByAdminObserver
        )

        roomSettingsViewModel.youModeratorLiveData.observe(viewLifecycleOwner, youModeratorObserver)

        peerConnectionViewModel.addToGalleryLiveData.observe(
            viewLifecycleOwner,
            addToGalleryObserver
        )

        peerConnectionViewModel.participantRemoveLiveData.observe(
            viewLifecycleOwner,
            participantRemoveObserver
        )
        peerConnectionViewModel.userSpeakingLiveData.observe(
            viewLifecycleOwner,
            currentlySpeakingObserver
        )
        peerConnectionViewModel.micEnabledLiveData.observe(
            viewLifecycleOwner,
            publisherMicObserver
        )
        peerConnectionViewModel.camEnabledLiveData.observe(
            viewLifecycleOwner,
            publisherCameraObserver
        )
    }

    private fun setupUI() {
        rvRenderers.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rvRenderers.viewTreeObserver.removeOnGlobalLayoutListener(this)
                adapter = RenderersAdapter(
                    resources,
                    onViewBind = { renderer, id ->
                        if (id == peerConnectionViewModel.publisherId) {
                            peerConnectionViewModel.setPublisherRenderer(renderer)
                        } else {
                            peerConnectionViewModel.setViewerRenderer(id, renderer)
                        }
                    }
                )
                rvRenderers.adapter = adapter
                rvRenderers.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
                rvRenderers.addItemDecoration(GridSpacingItemDecoration(spacing = DEFAULT_SPACING))
            }
        })
    }

    private val micToggleByAdminObserver = Observer<Nothing> {
        adapter.updateAudioStateFor(participantsViewModel.participants)
    }

    private val camToggleByAdminObserver = Observer<Nothing> {
        adapter.updateVideoStateFor(participantsViewModel.participants)
    }

    private val raiseHandObserver = Observer<Pair<Long, Boolean>> {
        participantsViewModel.participants.find { tmp -> tmp.userId == it.first }
            ?.let { participant ->
                if (it.second) {
                    participant.timeHandRaised = System.currentTimeMillis()
                } else {
                    participant.timeHandRaised = 0L
                }
                adapter.updateRaiseHandFor(participant)
            }
    }

    private val allHandsDownByAdminObserver = Observer<Nothing> {
        participantsViewModel.participants.forEach {
            adapter.updateRaiseHandFor(it)
        }
    }

    private val youModeratorObserver = Observer<Boolean> {
        if (this::adapter.isInitialized) {
            adapter.setIsModerator(it)
        }
    }

    private val addToGalleryObserver = Observer<Long> { newUserId ->
        participantsViewModel.participants.find { tmp ->
            tmp.userId == newUserId
        }?.let { participant ->
            if (participant.userId == peerConnectionViewModel.publisherId) {
                addPublisherFor(participant)
            } else {
                addRendererFor(participant)
            }
        }
    }

    private fun addPublisherFor(participant: KmeParticipant) {
        participant.micState = KmeMediaDeviceState.LIVE
        participant.webcamState = KmeMediaDeviceState.LIVE
        addRendererFor(participant)
    }

    private fun addRendererFor(participant: KmeParticipant) {
        adapter.apply {
            addRenderer(participant)
            invalidateSpans()
        }
    }

    private val participantRemoveObserver = Observer<Long> {
        it?.let { adapter.removeRendererFor(it) }
    }

    private val currentlySpeakingObserver = Observer<Pair<Long, Boolean>> {
        participantsViewModel.participants.find { tmp -> tmp.userId == it.first }
            ?.let { participant ->
                participant.isSpeaking = it.second
                adapter.updateSpeakingStateFor(participant)
            }
    }

    // Clear recycler pool to allow create new renderer from scratch
    private fun clearRecyclerViewPool() {
        rvRenderers.recycledViewPool.clear()
        rvRenderers.setRecycledViewPool(RecycledViewPool())
    }

    private val mediaStateChangedObserver = Observer<UserMediaStateChangedPayload> {
        participantsViewModel.participants.find { tmp -> tmp.userId == it.userId }
            ?.let { participant ->
                when (it.mediaStateType) {
                    KmeMediaStateType.MIC -> {
                        val state = it.stateValue
                        if (state != KmeMediaDeviceState.LIVE) {
                            participant.isSpeaking = false
                        }

                        participant.micState = state
                        adapter.updateAudioStateFor(participant)
                    }
                    KmeMediaStateType.WEBCAM -> {
                        participant.webcamState = it.stateValue
                        adapter.updateVideoStateFor(participant)
                    }
                    KmeMediaStateType.LIVE_MEDIA -> {
                        participant.liveMediaState = it.stateValue

                        participant.userId?.let {
                            when (participant.liveMediaState) {
                                KmeMediaDeviceState.UNLIVE,
                                KmeMediaDeviceState.MUTED_UNLIVE,
                                KmeMediaDeviceState.DISABLED_UNLIVE -> {
                                    adapter.removeRendererFor(it)
                                    peerConnectionViewModel.removePeerConnection(it.toString())
                                }
                                else -> {
                                }
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
    }

    private val publisherMicObserver = Observer<Boolean> {
        participantsViewModel.participants.find { tmp -> tmp.userId == viewModel.getPublisherId() }
            ?.let { participant ->
                participant.micState =
                    if (it) KmeMediaDeviceState.LIVE else KmeMediaDeviceState.DISABLED_LIVE
                if (!rvRenderers.isComputingLayout) {
                    adapter.updateAudioStateFor(participant)
                }
            }
    }

    private val publisherCameraObserver = Observer<Boolean> {
        participantsViewModel.participants.find { tmp -> tmp.userId == viewModel.getPublisherId() }
            ?.let { participant ->
                participant.webcamState =
                    if (it) KmeMediaDeviceState.LIVE else KmeMediaDeviceState.DISABLED_LIVE
                if (!rvRenderers.isComputingLayout) {
                    adapter.updateVideoStateFor(participant)
                }
            }
    }

    private fun invalidateSpans() {
        rvRenderers.post {
            (rvRenderers.layoutManager as StaggeredGridLayoutManager).invalidateSpanAssignments()
        }
    }

    companion object {

        private const val DEFAULT_SPACING = 6

        @JvmStatic
        fun newInstance() = RoomRenderersFragment()

    }

}
