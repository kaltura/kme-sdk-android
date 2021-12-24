package com.kme.kaltura.kmeapplication.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.data.MappedConversation
import com.kme.kaltura.kmeapplication.util.extensions.*
import com.kme.kaltura.kmeapplication.view.IBottomSheetCallback
import com.kme.kaltura.kmeapplication.view.dialog.ConnectionPreviewDialog
import com.kme.kaltura.kmeapplication.view.dialog.ConnectionPreviewDialog.PreviewListener
import com.kme.kaltura.kmeapplication.view.fragment.*
import com.kme.kaltura.kmeapplication.viewmodel.*
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.content.poll.KmeQuickPollView
import com.kme.kaltura.kmesdk.rest.response.room.KmeBaseRoom
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNote
import com.kme.kaltura.kmesdk.ws.message.KmeRoomExitReason
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.BannersPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeBannersModuleMessage.RoomPasswordStatusReceivedPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomInitModuleMessage.*
import com.kme.kaltura.kmesdk.ws.message.type.KmeContentType.*
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionKey
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.layout_room_bottom_controls.*
import kotlinx.android.synthetic.main.layout_room_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_room_main_tabs.*
import kotlinx.android.synthetic.main.toolbar_room.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RoomActivity : KmeActivity(), PreviewListener {

    private val viewModel: RoomStateViewModel by viewModel()
    private val recordingViewModel: RoomRecordingViewModel by viewModel()
    private val participantsViewModel: ParticipantsViewModel by viewModel()
    private val chatViewModel: ChatViewModel by viewModel()
    private val notesViewModel: RoomNoteViewModel by viewModel()
    private val roomInfoViewModel: RoomInfoViewModel by viewModel()
    private val roomSettingsViewModel: RoomSettingsViewModel by viewModel()
    private val conversationsViewModel: ConversationsViewModel by viewModel()
    private val peerConnectionViewModel: PeerConnectionViewModel by viewModel()

    private val roomAlias: String by lazy { intent?.getStringExtra(ROOM_ALIAS_EXTRA).toNonNull() }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ViewGroup>

    private var companyId: Long? = null
    private var roomId: Long? = null
    private var previewSettingsSet = false

    private var previewDialog: ConnectionPreviewDialog? = null
    private var alertDialog: AlertDialog? = null
    private var currentBottomSheetFragment: Fragment? = null
    private var contentFragment: Fragment? = null
    private var renderersFragment: RoomRenderersFragment? = null
    private var toolbarTitle: String? = null

    override fun onResume() {
        super.onResume()
        viewModel.reconnect()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setupRoomViewModel()
        setupUI()
    }

    private fun setupRoomViewModel() {
        companyId = intent?.extras?.get(COMPANY_ID_EXTRA) as Long?
        roomId = intent?.extras?.get(ROOM_ID_EXTRA) as Long?

        viewModel.isLoadingLiveData.observe(this, isLoadingObserver)

        viewModel.isConnectedLiveData.observe(this, isConnectedObserver)
        viewModel.roomStateLoadedLiveData.observe(this, roomStateLoadedObserver)
        viewModel.awaitApprovalLiveData.observe(this, awaitApprovalObserver)
        viewModel.userApprovedLiveData.observe(this, userApprovedObserver)
        viewModel.userRejectedLiveData.observe(this, userRejectedObserver)
        viewModel.anyInstructorsIsConnectedLiveData.observe(this, anyInstructorsIsConnectedObserver)
        viewModel.instructorIsOfflineLiveData.observe(this, instructorIsOfflineObserver)
        viewModel.roomHasPasswordLiveData.observe(this, roomHasPasswordObserver)
        viewModel.roomPasswordStatusLiveData.observe(this, roomPasswordStatusObserver)
        viewModel.roomParticipantLimitReachedLiveData.observe(
            this,
            roomParticipantLimitReachedObserver
        )
        viewModel.errorLiveData.observe(this, roomErrorObserver)
        viewModel.closeConnectionLiveData.observe(this, closeConnectionObserver)
        viewModel.youModeratorLiveData.observe(this, youModeratorObserver)
        viewModel.handRaisedLiveData.observe(this, raiseHandObserver)
        viewModel.sharedContentLiveData.observe(this, sharedContentObserver)

//        peerConnectionViewModel.publisherAddedLiveData.observe(this, publisherAddedObserver)
        peerConnectionViewModel.userSpeakingLiveData.observe(this, currentlySpeakingObserver)
        peerConnectionViewModel.speakerEnabledLiveData.observe(this, speakerObserver)
        peerConnectionViewModel.micEnabledLiveData.observe(this, micObserver)
        peerConnectionViewModel.camEnabledLiveData.observe(this, cameraObserver)
        peerConnectionViewModel.liveEnabledLiveData.observe(this, liveObserver)

        chatViewModel.subscribe()

        ifNonNull(companyId, roomId) { companyId, roomId ->
            viewModel.connect(companyId, roomId, roomAlias)
        } ?: run {
            setupRoomInfoViewModel()
        }
    }

    private fun setupRoomInfoViewModel() {
        roomInfoViewModel.fetchRoomInfo(roomAlias)
        roomInfoViewModel.isLoadingLiveData.observe(this, isLoadingObserver)
        roomInfoViewModel.roomInfoLiveData.observe(this, roomInfoObserver)
        roomInfoViewModel.roomInfoErrorLiveData.observe(this, roomErrorObserver)
    }

    private fun setupRoomSettingsViewModel() {
        roomSettingsViewModel.chatSettingsChangedLiveData.observe(this, chatSettingsChangedObserver)
        roomSettingsViewModel.youModeratorLiveData.observe(this, youModeratorObserver)
        roomSettingsViewModel.toggleAllMicsByAdminLiveData.observe(
            this,
            toggleAllMicsByAdminObserver
        )
        roomSettingsViewModel.toggleAllCamsByAdminLiveData.observe(
            this,
            toggleAllCamsByAdminObserver
        )
        roomSettingsViewModel.subscribe()
    }

    private fun setupParticipantsViewModel() {
        participantsViewModel.micToggledByAdminLiveData.observe(this, toggleMicByAdminObserver)
        participantsViewModel.camToggledByAdminLiveData.observe(this, toggleCamByAdminObserver)
        participantsViewModel.liveToggledByAdminLiveData.observe(this, toggleLiveByAdminObserver)
        participantsViewModel.allHandsDownByAdminLiveData.observe(
            this,
            disableAllRaiseHandsByAdminObserver
        )
        participantsViewModel.subscribe()
    }

    private fun setupConversationsViewModel() {
        conversationsViewModel.allUnreadMessageCounterLiveData.observe(
            this,
            allUnreadMessageCounterObserver
        )
        conversationsViewModel.openConversationLiveData.observe(this, onOpenConversationObserver)
        conversationsViewModel.closeConversationLiveData.observe(this, onCloseConversationObserver)
    }

    private fun setupNotesViewModel() {
        notesViewModel.openNoteLiveData.observe(this, openNoteObserver)
        notesViewModel.broadcastNoteLiveData.observe(this, broadcastNoteObserver)
    }

    private fun setupRecordingViewModel() {
        recordingViewModel.recordStateInitializingLiveData.observe(this, recordInitializingObserver)
        recordingViewModel.recordStateReadyToStartLiveData.observe(this, recordReadyToStartObserver)
        recordingViewModel.recordStateReadyToStopLiveData.observe(this, recordReadyToStopObserver)
        recordingViewModel.recordStartingLiveData.observe(this, recordStartingObserver)
        recordingViewModel.recordInitiatedLiveData.observe(this, recordInitiatedObserver)
        recordingViewModel.recordLiveData.observe(this, recordingObserver)
        recordingViewModel.recordCompletedLiveData.observe(this, recordCompletedObserver)
        recordingViewModel.conversionCompletedLiveData.observe(this, conversionCompletedObserver)
        recordingViewModel.uploadCompletedLiveData.observe(this, recordUploadObserver)
        recordingViewModel.recordDurationLiveData.observe(this, recordDurationObserver)
        recordingViewModel.recordFailedLiveData.observe(this, recordFailedObserver)
        recordingViewModel.subscribe()
    }

    private fun setupUI() {
        setupToolbar()
        setupBottomSheet()
        setupQuickPollView()
        setupBackStackChanged()

        renderersFragment = RoomRenderersFragment.newInstance()
        renderersFragment?.let {
            replaceFragment(it, renderersFrame.id)
        }
    }

    private fun setupToolbar() {
        setCustomToolbar(toolbar)
        hideHomeButton()

        btnLeaveRoom.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupBottomControls() {
        btnMenu.setOnClickListener {
            it.popup(R.menu.menu_room_tools, false).apply {
                menu.findItem(R.id.action_recording).isVisible =
                    recordingViewModel.isRecordingEnabled()
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem?.itemId) {
                        R.id.action_youtube -> {

                        }
                        R.id.action_whiteboard -> {

                        }
                        R.id.action_desk_share -> {

                        }
                        R.id.action_quizzes -> {

                        }
                        R.id.action_recording -> {
                            recordingViewModel.askForRecordingAction()
                        }
                        R.id.action_breakout -> {

                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                show()
            }
        }
        btnToggleSpeaker.setOnClickListener {
            peerConnectionViewModel.toggleSpeaker()
        }

        btnToggleMicro.setOnClickListener {
            if (previewSettingsSet) {
                peerConnectionViewModel.toggleMic()
            } else {
                showPreviewSettings()
            }
        }

        btnToggleCamera.setOnClickListener {
            if (previewSettingsSet) {
                peerConnectionViewModel.toggleCamera()
            } else {
                showPreviewSettings()
            }
        }
    }

    private fun updateRoleDependUI(isAdmin: Boolean) {
        btnRaiseHand.apply {
            if (!isAdmin) {
                visible()
                setOnClickListener {
                    viewModel.raiseHand()
                }
            } else gone()
        }
        btnMenu.visibility = if (isAdmin) VISIBLE else GONE

        recIndicators.apply {
            if (isAdmin) {
                setOnClickListener { showStopRecDialog() }
            } else {
                setOnClickListener(null)
            }
        }
    }

    private fun setupBackStackChanged() {
        supportFragmentManager.addOnBackStackChangedListener {
            val chatFragment = getFragmentByTag(ChatFragment::class.java.simpleName)
            if (chatFragment != null && chatFragment.isAdded && chatFragment.isVisible) {
                chatFragment.onResume()
            }
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = from(bottomSheetContainer)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_HIDDEN -> {
                        hideKeyboard()
                        roomTabLayout.selectTab(null)
                        roomTabLayout.setSelectedTabIndicatorColor(
                            ContextCompat.getColor(
                                this@RoomActivity,
                                R.color.transparent
                            )
                        )
                        for (fragment in supportFragmentManager.fragments) {
                            if (fragment is IBottomSheetCallback) {
                                fragment.onBottomSheetClosed()
                            }
                        }
                    }
                    STATE_EXPANDED -> {
                        roomTabLayout.setSelectedTabIndicatorColor(
                            ContextCompat.getColor(
                                this@RoomActivity,
                                R.color.white
                            )
                        )
                        for (fragment in supportFragmentManager.fragments) {
                            if (fragment is IBottomSheetCallback) {
                                fragment.onBottomSheetOpened()
                            }
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        roomTabLayout.setTabsClickable(false)
        roomTabLayout.selectTab(null)
        roomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    TAB_CHAT_POSITION -> showConversationFragment()
                    TAB_PARTICIPANT_POSITION -> showParticipantsFragment()
                    TAB_NOTES_POSITION -> showNotesFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun isBottomSheetShown(): Boolean = bottomSheetBehavior.state == STATE_EXPANDED

    private fun showBottomSheet() {
        bottomSheetBehavior.state = STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = STATE_HIDDEN
    }

    private fun TabLayout?.setTabsClickable(isClickable: Boolean) {
        this?.let {
            for (i in 0..it.tabCount) {
                it.getTabAt(i)?.view?.isClickable = isClickable
            }
        }
    }

    private fun TabLayout.Tab?.updateBadge(number: Int) {
        if (number <= 0) {
            this?.removeBadge()
        } else {
            this?.orCreateBadge?.apply {
                this.number = number
                maxCharacterCount = 3
                isVisible = true
                backgroundColor = ContextCompat.getColor(this@RoomActivity, R.color.colorAccent)
            }
        }
    }

    private fun setupQuickPollView() {
        quickPollView.init(this, KmeQuickPollView.Config())
    }

    private fun showConversationFragment() {
        if (currentBottomSheetFragment !is ConversationsFragment) {
            ifNonNull(companyId, roomId) { companyId, roomId ->
                currentBottomSheetFragment = ConversationsFragment.newInstance(
                    companyId,
                    roomId,
                    conversationsViewModel.conversationsLiveData.value ?: arrayListOf()
                )
            }
            replaceFragment(currentBottomSheetFragment as ConversationsFragment, frame.id)
        }
        showBottomSheet()
    }

    private fun showParticipantsFragment() {
        if (currentBottomSheetFragment !is ParticipantsFragment) {
            currentBottomSheetFragment = ParticipantsFragment.newInstance()
            replaceFragment(currentBottomSheetFragment as ParticipantsFragment, frame.id)
        }
        showBottomSheet()
    }

    private fun showNotesFragment() {
        if (currentBottomSheetFragment !is RoomNoteListFragment) {
            currentBottomSheetFragment = RoomNoteListFragment.newInstance()
            replaceFragment(currentBottomSheetFragment as RoomNoteListFragment, frame.id)
        }
        showBottomSheet()
    }

    private val openNoteObserver = Observer<KmeRoomNote> {
        if (currentBottomSheetFragment is RoomNoteListFragment) {
            popBackStack()
            replaceFragment(
                RoomNoteFragment.newInstance(it),
                frame.id,
                addToStack = true
            )
        }
        hideKeyboard()
    }

    private val broadcastNoteObserver = Observer<KmeRoomNote> {
        showBottomSheet()
        if (currentBottomSheetFragment !is RoomNoteFragment &&
            currentBottomSheetFragment !is RoomNoteListFragment
        ) {
            roomTabLayout.getTabAt(TAB_NOTES_POSITION)?.select()
            showNotesFragment()
            openNoteObserver.onChanged(it)
        }
    }

    private val isLoadingObserver = Observer<Boolean> {
        if (it) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    private val roomInfoObserver = Observer<KmeBaseRoom> {
        companyId = it.companyId
        roomId = it.id

        ifNonNull(companyId, roomId) { companyId, roomId ->
            viewModel.connect(companyId, roomId, roomAlias)
        }

        if (toolbarTitle == null) {
            toolbarTitle = it.name
            toolbarTitle(toolbarTitle ?: getString(R.string.room))
        }
    }

    private val roomStateLoadedObserver = Observer<RoomStatePayload> {
        setupConversationsViewModel()
        setupNotesViewModel()
        roomTabLayout.setTabsClickable(true)
        setupBottomControls()

        // Set participants first
        participantsViewModel.setRoomState(it)
        peerConnectionViewModel.setRoomState(it)

        showPreviewSettings()
    }

    private val isConnectedObserver = Observer<Boolean> {
        setupRoomSettingsViewModel()
        setupParticipantsViewModel()
        setupRecordingViewModel()

        ifNonNull(companyId, roomId) { companyId, roomId ->
            peerConnectionViewModel.setRoomData(companyId, roomId)
            participantsViewModel.setRoomData(companyId, roomId)
            notesViewModel.setRoomData(companyId, roomId)
            recordingViewModel.setRoomData(companyId, roomId)
        }
    }

    private val allUnreadMessageCounterObserver = Observer<Int> {
        roomTabLayout.getTabAt(TAB_CHAT_POSITION).updateBadge(it)
    }

    private val onOpenConversationObserver = Observer<MappedConversation> {
        hideKeyboard()
        ifNonNull(companyId, roomId) { companyId, roomId ->
            replaceFragment(
                ChatFragment.newInstance(it, roomId, companyId),
                frame.id,
                addToStack = true
            )
        }
    }

    private val anyInstructorsIsConnectedObserver =
        Observer<KmeRoomInitModuleMessage<AnyInstructorsIsConnectedToRoomPayload>?> {
            alertDialog.hideIfExist()
            alertDialog = null
        }

    private val userApprovedObserver =
        Observer<KmeRoomInitModuleMessage<ApprovalPayload>?> {
            alertDialog.hideIfExist()
            alertDialog = null
        }

    private val userRejectedObserver =
        Observer<KmeRoomInitModuleMessage<ApprovalPayload>?> {
            alertDialog.hideIfExist()
            alertDialog = null
        }

    private val awaitApprovalObserver =
        Observer<KmeRoomInitModuleMessage<ApprovalPayload>?> {
            alertDialog.hideIfExist()
            alertDialog = alert(R.string.please_wait, R.string.request_to_join_sent) {
                positiveButton(R.string.leave) {
                    finish()
                }
                cancelable = false
            }
            alertDialog?.show()
        }

    private val instructorIsOfflineObserver =
        Observer<KmeRoomInitModuleMessage<InstructorIsOfflinePayload>?> {
            alertDialog.hideIfExist()
            alertDialog = alert(R.string.please_wait, R.string.instructor_not_arrived) {
                positiveButton(R.string.leave) {
                    finish()
                }
                cancelable = false
            }
            alertDialog?.show()
        }

    private val roomHasPasswordObserver =
        Observer<KmeBannersModuleMessage<BannersPayload>?> {
            alertDialog.hideIfExist()
            alertDialog = alert(R.string.enter_password, R.string.password_required, true) {
                positiveButton(R.string.submit) {
                    viewModel.submitPassword(inputPassword.text.toString())
                }
                cancelable = false
            }
            alertDialog?.show()
        }

    private val roomParticipantLimitReachedObserver =
        Observer<KmeRoomInitModuleMessage<RoomParticipantLimitReachedPayload>?> {
            alertDialog.hideIfExist()
            alertDialog = alert(R.string.disconnected, R.string.participant_limit_reached) {
                positiveButton(R.string.leave) {
                    finish()
                }
                cancelable = false
            }
            alertDialog?.show()
        }

    private val roomPasswordStatusObserver =
        Observer<KmeBannersModuleMessage<RoomPasswordStatusReceivedPayload>?> {
            if (it == null || it.payload?.status == false) {
                Snackbar.make(root, R.string.incorrect_password, Snackbar.LENGTH_SHORT).show()
            }
        }

    private val roomErrorObserver = Observer<String?> {
        Snackbar.make(root, it ?: getString(R.string.error), Snackbar.LENGTH_SHORT).show()
    }

    private val closeConnectionObserver = Observer<KmeRoomExitReason> { reason ->
            previewDialog?.dismiss()
            closePreviewSettings()
            alertDialog.hideIfExist()
            alertDialog = when (reason) {
                KmeRoomExitReason.DUPLICATED_TAB -> {
                    alert(R.string.disconnected, R.string.duplicated_tab) {
                        positiveButton(R.string.leave) {
                            finish()
                        }
                        cancelable = false
                    }
                }
                KmeRoomExitReason.REMOVED_USER -> {
                    alert(R.string.disconnected, R.string.removed_from_the_room) {
                        positiveButton(R.string.leave) {
                            finish()
                        }
                        cancelable = false
                    }
                }
                KmeRoomExitReason.USER_LEAVE_SESSION -> {
                    alert(R.string.session_ended, R.string.left_session) {
                        negativeButton(R.string.leave) {
                            finish()
                        }
                        positiveButton(R.string.rejoin) {

                        }
                    }
                }
                KmeRoomExitReason.INSTRUCTOR_ENDED_SESSION -> {
                    alert(R.string.session_ended, R.string.instructor_ended_session) {
                        positiveButton(R.string.leave) {
                            finish()
                        }
                        cancelable = false
                    }
                }
                else -> null
            }
            alertDialog?.show()
        }

//    private val publisherAddedObserver = Observer<Boolean> {
//        btnToggleCamera.isEnabled = it
//    }

    private val currentlySpeakingObserver = Observer<Pair<Long, Boolean>> {
        participantsViewModel.participants.find { tmp -> tmp.userId == it.first }
            ?.let { participant ->
                val speaker = if (it.second) participant.fullName else ""
                tvSpeaker.text = speaker
            }
    }

    private val toggleMicByAdminObserver = Observer<Boolean> {
        peerConnectionViewModel.toggleMicByAdmin(it, false)
    }

    private val toggleCamByAdminObserver = Observer<Boolean> {
        peerConnectionViewModel.toggleCamByAdmin(it, false)
    }

    private val toggleLiveByAdminObserver = Observer<Boolean> {
        peerConnectionViewModel.toggleLiveByAdmin(it)
    }

    private val raiseHandObserver = Observer<Boolean> {
        btnRaiseHand.isSelected = it
    }

    private val sharedContentObserver = Observer<KmeContentView> {
        it?.let {
            contentFragment = it
            showContentFragment()
        } ?: run {
            hideContentFragment()
        }
    }

    private fun showContentFragment() {
        contentFragment?.let {
            contentFrame.visible()
            replaceFragment(it, contentFrame.id)
        } ?: run {
            contentFrame.gone()
        }
    }

    private fun hideContentFragment() {
        contentFragment?.let { fragment ->
            removeFragment(fragment)
        }
        contentFragment = null
        contentFrame.gone()
    }

    private val speakerObserver = Observer<Boolean> {
        btnToggleSpeaker.isSelected = it
    }

    private val micObserver = Observer<Boolean> {
        btnToggleMicro.isSelected = it
    }

    private val cameraObserver = Observer<Boolean> {
        btnToggleCamera.isSelected = it
    }

    private val liveObserver = Observer<Boolean> {
        if (it) {
            participantsViewModel.updatePublisherLive()
        }
    }

    private val chatSettingsChangedObserver =
        Observer<Pair<KmePermissionKey?, KmePermissionValue?>> {
            conversationsViewModel.onChatSettingsChanged(it)
        }

    private val toggleAllMicsByAdminObserver = Observer<Boolean> { disable ->
        participantsViewModel.toggleAllMicsByAdmin(!disable)
        peerConnectionViewModel.toggleMicByAdmin(!disable, true)
    }

    private val toggleAllCamsByAdminObserver = Observer<Boolean> { disable ->
        participantsViewModel.toggleAllCamsByAdmin(!disable)
        peerConnectionViewModel.toggleCamByAdmin(!disable, true)
    }

    private val disableAllRaiseHandsByAdminObserver = Observer<Nothing> {
        btnRaiseHand.isSelected = false
        viewModel.putHandDownByAdmin()
    }

    private val youModeratorObserver = Observer<Boolean> {
        updateRoleDependUI(it)
    }

    private val onCloseConversationObserver = Observer<MappedConversation?> {
        val chatFragment = getFragmentByTag(ChatFragment::class.java.simpleName)
        if (chatFragment != null && chatFragment.isVisible && chatFragment.isAdded) {
            removeFragment(chatFragment)
        }
    }

    private val recordInitializingObserver = Observer<Nothing> {
        showRecInitializingDialog()
    }

    private val recordReadyToStartObserver = Observer<List<Long>> {
        showStartRecDialog(it)
    }

    private val recordReadyToStopObserver = Observer<Nothing> {
        showStopRecDialog()
    }

    private val recordStartingObserver = Observer<Boolean> {
        alertDialog.hideIfExist()
        Snackbar.make(root, R.string.recording_status_initiated, Snackbar.LENGTH_SHORT).show()
        recProgressBar.visible()
    }

    private val recordInitiatedObserver = Observer<Nothing> {
        recProgressBar.visible()
    }

    private val recordingObserver = Observer<Boolean> {
        alertDialog.hideIfExist()
        recIndicators.visibility = if (it) VISIBLE else INVISIBLE
        recProgressBar.gone()
        if (it) {
            Snackbar.make(root, R.string.recording_status_started, Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(root, R.string.recording_status_stopped, Snackbar.LENGTH_SHORT).show()
        }
    }

    private val recordCompletedObserver = Observer<Nothing> {
        recIndicators.invisible()
        recProgressBar.gone()
        Snackbar.make(root, R.string.recording_status_processing, Snackbar.LENGTH_SHORT).show()
    }

    private val conversionCompletedObserver = Observer<Nothing> {
        Snackbar.make(root, R.string.recording_status_uploading, Snackbar.LENGTH_SHORT).show()
    }

    private val recordUploadObserver = Observer<Nothing> {
        Snackbar.make(root, R.string.recording_status_finished, Snackbar.LENGTH_SHORT).show()
    }

    private val recordDurationObserver = Observer<Long> {
        if (it != null) {
            tvRecDuration.setSeconds(it)
        }
    }

    private val recordFailedObserver = Observer<String> {
        Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
        recProgressBar.gone()
    }

    private fun showRecInitializingDialog() {
        alertDialog = alert(
            R.string.recording_initializing_dialog_title,
            R.string.recording_initializing_dialog_message,
        ) {
            positiveButton(R.string.recording_initializing_action_positive)
        }
        alertDialog?.show()
    }

    private fun showStartRecDialog(variants: List<Long>) {
        val variantsStr = recordingViewModel.mapAvailableVariants(
            variants,
            getString(R.string.recording_start_dialog_prefix),
            getString(R.string.recording_start_dialog_postfix_minutes),
            getString(R.string.recording_start_dialog_postfix_hour),
            getString(R.string.recording_start_dialog_postfix_hours)
        )

        alertDialog = radioDialog(
            titleResource = R.string.recording_start_dialog_title,
            variants = variantsStr
        ) {
            positiveButton(R.string.recording_start_dialog_action_positive) {
                recordingViewModel.startRecording(variants[radioGroup.checkedRadioButtonId])
            }
            negativeButton(R.string.recording_start_dialog_action_negative)
        }
        alertDialog?.show()
    }

    private fun showStopRecDialog() {
        alertDialog = alert(
            R.string.recording_stop_dialog_title,
            R.string.recording_stop_dialog_message
        ) {
            positiveButton(R.string.recording_stop_dialog_action_positive) {
                recordingViewModel.stopRecording()
            }
            negativeButton(R.string.recording_stop_dialog_action_negative)
        }
        alertDialog?.show()
    }

    override fun onBackPressed() {
        if (isBottomSheetShown()) {
            hideBottomSheet()
        } else {
            alert(R.string.end_session, R.string.end_session_message) {
                if (viewModel.isModerator()) {
                    neutralButton(R.string.end_session_for_everyone) {
                        viewModel.endSessionForEveryone()
                        super.onBackPressed()
                    }
                }

                positiveButton(R.string.leave) {
                    viewModel.endSession()
                    super.onBackPressed()
                }
            }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog.hideIfExist()
        alertDialog = null
        closePreviewSettings()
    }

    private fun showPreviewSettings() {
        if (hasPermissions(PERMISSIONS)) {
            previewDialog = ConnectionPreviewDialog.newInstance()
            previewDialog?.setListener(this)
            previewDialog?.show(supportFragmentManager, ConnectionPreviewDialog.TAG)
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, CODE_PERMISSION_ALL)
        }
    }

    private fun closePreviewSettings() {
        previewDialog?.dismiss()
        previewDialog = null
    }

    override fun onPreviewSettingsAccepted(
        micEnabled: Boolean,
        camEnabled: Boolean,
        frontCamEnabled: Boolean
    ) {
        previewSettingsSet = true
        peerConnectionViewModel.startPublish(
            micEnabled,
            camEnabled,
            frontCamEnabled
        )
    }

    private fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_PERMISSION_ALL) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }

            previewDialog = ConnectionPreviewDialog.newInstance()
            previewDialog?.setListener(this)
            previewDialog?.show(supportFragmentManager, ConnectionPreviewDialog.TAG)
        }
    }

    companion object {
        const val ROOM_ALIAS_EXTRA = "ROOM_ALIAS_EXTRA"
        const val COMPANY_ID_EXTRA = "COMPANY_ID_EXTRA"
        const val ROOM_ID_EXTRA = "ROOM_ID_EXTRA"

        private const val CODE_PERMISSION_ALL = 1

        private const val TAB_CHAT_POSITION = 0
        private const val TAB_PARTICIPANT_POSITION = 1
        private const val TAB_NOTES_POSITION = 2

        var PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )

        fun Context.openRoomActivity(
            companyId: Long,
            roomId: Long,
            roomAlias: String
        ) {
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra(COMPANY_ID_EXTRA, companyId)
            intent.putExtra(ROOM_ID_EXTRA, roomId)
            intent.putExtra(ROOM_ALIAS_EXTRA, roomAlias)
            startActivity(intent)
        }

        fun Context.openRoomActivity(roomAlias: String) {
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra(ROOM_ALIAS_EXTRA, roomAlias)
            startActivity(intent)
        }
    }

}