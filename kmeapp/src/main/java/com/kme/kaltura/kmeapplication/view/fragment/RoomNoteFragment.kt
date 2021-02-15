package com.kme.kaltura.kmeapplication.view.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.popBackStack
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.viewmodel.RoomNoteViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomSettingsViewModel
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNote
import kotlinx.android.synthetic.main.fragment_room_note.*
import kotlinx.android.synthetic.main.layout_room_bottom_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RoomNoteFragment : Fragment() {

    private val viewModel: RoomNoteViewModel by sharedViewModel()
    private val settingsViewModel: RoomSettingsViewModel by sharedViewModel()

    private val roomNote: KmeRoomNote? by lazy { arguments?.getParcelable(ROOM_NOTE_EXTRA) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdminPanel()
        setupToolbar()
        setupViewModel()
    }

    private fun setupAdminPanel() {
        btnBold.setOnClickListener {
        }
        btnItalic.setOnClickListener {
        }
        btnUnderline.setOnClickListener {
        }

        btnUnordered.setOnClickListener {
        }
        btnOrdered.setOnClickListener {
        }

        btnBroadcastNote.setOnClickListener {
            roomNote?.let {
                viewModel.broadcastNote(it)
            }
        }
    }

    private fun setupToolbar() {
        activity?.sheetActionBarContainer.visible()
        activity?.btnBroadcastNote?.setOnClickListener {
            roomNote?.let {
                viewModel.broadcastNote(it)
            }
        }
        activity?.tvSheetBarTitle?.text = roomNote?.name
        activity?.btnSheetBack?.setOnClickListener {
            activity?.sheetActionBarContainer.gone()
            view?.animate()?.apply {
                duration = 250L
                interpolator = AccelerateDecelerateInterpolator()
                alpha(0f)
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        removeFragment()
                    }
                })
                start()
            } ?: run {
                removeFragment()
            }
        }
    }

    private fun setupViewModel() {
        roomNote?.let { note ->
            note.content?.let {
                viewModel.setNoteContent(it)
            } ?: run {
                viewModel.setNoteContent("")
            }

            viewModel.adminControlsLiveData.observe(viewLifecycleOwner, adminControlsObserver)
            settingsViewModel.youModeratorLiveData.observe(
                viewLifecycleOwner,
                adminControlsObserver
            )

            note.id?.let {
                viewModel.subscribeToNoteChanges(it, true)
            }
            viewModel.noteTextChangedLiveData.observe(viewLifecycleOwner, noteTextChangedObserver)

            viewModel.deletedNoteLiveData.observe(viewLifecycleOwner, deletedNoteObserver)
        }
    }

    private fun removeFragment() {
        (activity as AppCompatActivity).popBackStack()
    }

    private val adminControlsObserver = Observer<Boolean> {
        edtNoteText.isEnabled = false /*it*/
        adminPanel?.visibility = if (it) VISIBLE else GONE
    }

    private val noteTextChangedObserver = Observer<SpannableString> {
        edtNoteText.setText(it)
    }

    private val deletedNoteObserver = Observer<String> {
        if (it == roomNote?.id) {
            removeFragment()
        }
    }

    override fun onDestroyView() {
        activity?.sheetActionBarContainer.gone()
        roomNote?.id?.let {
            viewModel.subscribeToNoteChanges(it, false)
        }
        super.onDestroyView()
    }

    companion object {
        const val ROOM_NOTE_EXTRA = "ROOM_NOTE_EXTRA"

        @JvmStatic
        fun newInstance(
            roomNote: KmeRoomNote
        ) = RoomNoteFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ROOM_NOTE_EXTRA, roomNote)
            }
        }
    }

}
