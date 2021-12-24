package com.kme.kaltura.kmeapplication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.*
import com.kme.kaltura.kmeapplication.view.adapter.ParticipantsAdapter
import com.kme.kaltura.kmeapplication.viewmodel.ParticipantsViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomSettingsViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomStateViewModel
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.android.synthetic.main.fragment_participants.*
import kotlinx.android.synthetic.main.layout_room_admin_controls.*
import kotlinx.android.synthetic.main.layout_search_view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ParticipantsFragment : Fragment() {

    private val viewModel: ParticipantsViewModel by sharedViewModel()
    private val roomViewModel: RoomStateViewModel by sharedViewModel()
    private val roomSettingsViewModel: RoomSettingsViewModel by sharedViewModel()

    private lateinit var adapter: ParticipantsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_participants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()
        setupSearchView()
        setupAdminControls()
        setupViewModel()
        updateData()
    }

    private fun setupList() {
        adapter = ParticipantsAdapter()

        rvParticipants.layoutManager = LinearLayoutManager(context)
        rvParticipants.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        rvParticipants.adapter = adapter
    }

    private fun setupSearchView() {
        btnClearText.setOnClickListener {
            etSearch.text = null
        }
        etSearch.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                btnClearText.invisible()
            } else {
                btnClearText.visible()
            }
            adapter.filter.filter(it)
        }
    }

    private fun setupAdminControls() {
        ivPutAllHands.isEnabled = false
        ivPutAllHands.setOnClickListener {
            viewModel.putAllHandsDown()
        }
        ivPublicChat.setOnClickListener {
            viewModel.togglePublicChat()
        }
        ivAllCams.setOnClickListener {
            viewModel.toggleAllCams()
        }
        ivAllMics.setOnClickListener {
            viewModel.toggleAllMics()
        }
    }

    private fun setupViewModel() {
        viewModel.allMicsStateLiveData.observe(viewLifecycleOwner, allMicsStateObserver)
        viewModel.allCamsStateLiveData.observe(viewLifecycleOwner, allCamsStateObserver)
        viewModel.publicChatStateLiveData.observe(viewLifecycleOwner, publicChatStateObserver)

        viewModel.userDisconnectedLiveData.observe(viewLifecycleOwner, userDisconnectedObserver)
        viewModel.participantChangedLiveData.observe(viewLifecycleOwner, participantChangedObserver)

        viewModel.mediaStateChangedByAdminLiveData.observe(
            viewLifecycleOwner,
            mediaStateChangedByAdminByAdminObserver
        )
        viewModel.participantHandRaisedLiveData.observe(viewLifecycleOwner, participantHandRaisedObserver)
        viewModel.anyHandsRaisedStateLiveData.observe(viewLifecycleOwner, anyHandsRaisedObserver)
        viewModel.allHandsDownByAdminLiveData.observe(viewLifecycleOwner, disableAllRaiseHandsByAdminObserver)

        roomViewModel.youModeratorLiveData.observe(viewLifecycleOwner, youModeratorObserver)

        roomSettingsViewModel.youModeratorLiveData.observe(viewLifecycleOwner, youModeratorObserver)
        roomSettingsViewModel.moderatorLiveData.observe(viewLifecycleOwner, moderatorChangedObserver)
        roomSettingsViewModel.chatSettingsChangedLiveData.observe(viewLifecycleOwner, chatSettingsChangedObserver)
    }

    private fun updateData() {
        adapter.setParticipants(viewModel.participants)
    }

    private val onParticipantClick: (view: View, participant: KmeParticipant) -> Unit =
        { view: View, participant: KmeParticipant ->
            view.popup(R.menu.menu_room_participants, false).apply {
                menu.forEach { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_toggle_micro -> menuItem.title =
                            if (participant.micState == KmeMediaDeviceState.UNLIVE ||
                                participant.micState == KmeMediaDeviceState.DISABLED_UNLIVE
                            ) {
                                getString(R.string.enable_mic)
                            } else {
                                getString(R.string.disable_mic)
                            }
                        R.id.action_toggle_cam -> menuItem.title =
                            if (participant.webcamState == KmeMediaDeviceState.UNLIVE ||
                                participant.webcamState == KmeMediaDeviceState.DISABLED_UNLIVE
                            ) {
                                getString(R.string.enable_cam)
                            } else {
                                getString(R.string.disable_cam)
                            }
                        R.id.action_toggle_live -> menuItem.title =
                            if (participant.liveMediaState == KmeMediaDeviceState.UNLIVE ||
                                participant.liveMediaState == KmeMediaDeviceState.DISABLED_UNLIVE
                            ) {
                                getString(R.string.enable_live)
                            } else {
                                getString(R.string.disable_live)
                            }
                    }
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem?.itemId) {
                        R.id.action_toggle_micro -> {
                            viewModel.toggleParticipantMicro(participant)
                        }
                        R.id.action_toggle_cam -> {
                            viewModel.toggleParticipantCam(participant)
                        }
                        R.id.action_toggle_live -> {
                            viewModel.toggleParticipantLive(participant)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                show()
            }
        }

    private fun updateRoleDependUI(isAdmin: Boolean) {
        if (isAdmin) {
            adminControls.visible()
            viewModel.updateAdminPanelState()
        } else {
            adminControls.gone()
        }
    }

    private val youModeratorObserver = Observer<Boolean> { isAdmin ->
        adapter.onParticipantClick = if (isAdmin) onParticipantClick else null
        updateRoleDependUI(isAdmin)
    }

    private val allMicsStateObserver = Observer<KmePermissionValue> {
        ivAllMics.isSelected = it == KmePermissionValue.OFF
    }

    private val allCamsStateObserver = Observer<KmePermissionValue> {
        ivAllCams.isSelected = it == KmePermissionValue.OFF
    }

    private val publicChatStateObserver = Observer<KmePermissionValue> {
        ivPublicChat.isSelected = it == KmePermissionValue.OFF
    }

    private val moderatorChangedObserver = Observer<Pair<Long, Boolean>> {
        viewModel.updateUserModeratorState(it.first, it.second)
        updateData()
    }

    private val chatSettingsChangedObserver = Observer<Pair<KmePermissionKey?, KmePermissionValue?>> {
        if (it.first == KmePermissionKey.PUBLIC_CHAT) {
            ivPublicChat.isSelected = it.second == KmePermissionValue.OFF
        }
    }

    private val participantChangedObserver = Observer<KmeParticipant> {
        adapter.addOrUpdateParticipant(it)
    }

    private val userDisconnectedObserver = Observer<Long> {
        adapter.removeParticipant(it)
    }

    private val mediaStateChangedByAdminByAdminObserver = Observer<Nothing> {
        updateData()
    }

    private val disableAllRaiseHandsByAdminObserver = Observer<Nothing> {
        ivPutAllHands.isSelected = false
        ivPutAllHands.isEnabled = false
        updateData()
    }

    private val participantHandRaisedObserver = Observer<Pair<Long, Boolean>> {
        updateData()
    }

    private val anyHandsRaisedObserver = Observer<Boolean> {
        ivPutAllHands.isSelected = it
        ivPutAllHands.isEnabled = it
    }

    override fun onPause() {
        super.onPause()
        etSearch.clearFocus()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ParticipantsFragment()
    }

}
