package com.kme.kaltura.kmeapplication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.alert
import com.kme.kaltura.kmeapplication.util.extensions.ifNonNull
import com.kme.kaltura.kmeapplication.util.extensions.popup
import com.kme.kaltura.kmeapplication.view.adapter.RoomNotesAdapter
import com.kme.kaltura.kmeapplication.viewmodel.RoomNoteViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomSettingsViewModel
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNote
import kotlinx.android.synthetic.main.fragment_participants.*
import kotlinx.android.synthetic.main.fragment_room_note_list.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RoomNoteListFragment : Fragment() {

    private val viewModel: RoomNoteViewModel by sharedViewModel()
    private val settingsViewModel: RoomSettingsViewModel by sharedViewModel()

    private lateinit var adapter: RoomNotesAdapter
    private var isAdmin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupList()
        setupViewModel()
    }

    private fun setupUI() {
        fabCreateNote.setOnClickListener { viewModel.createNote() }
    }

    private fun setupList() {
        adapter = RoomNotesAdapter()

        rvRoomNotes.layoutManager = LinearLayoutManager(context)
        rvRoomNotes.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        rvRoomNotes.adapter = adapter

        adapter.onNoteActionsClick = onNoteActionsClick
        adapter.onNoteClick = onNoteClick
    }

    private fun setupViewModel() {
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner, isLoadingObserver)
        viewModel.roomNotesLiveData.observe(viewLifecycleOwner, roomNotesObserver)
        viewModel.adminControlsLiveData.observe(viewLifecycleOwner, adminControlsObserver)
        viewModel.addToFilesStateLiveData.observe(viewLifecycleOwner, addToFilesObserver)
        viewModel.downloadStateLiveData.observe(
            viewLifecycleOwner,
            noteDownloadObserver
        )
        viewModel.deleteNoteStateLiveData.observe(viewLifecycleOwner, deleteStateObserver)

        settingsViewModel.youModeratorLiveData.observe(
            viewLifecycleOwner,
            adminControlsObserver
        )

        viewModel.getRoomNotes()
    }

    private val onNoteActionsClick: (view: View, roomNote: KmeRoomNote) -> Unit =
        { view: View, roomNote: KmeRoomNote ->
            view.popup(R.menu.menu_room_notes, false).apply {
                if (!isAdmin) {
                    menu.forEach { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_rename,
                            R.id.action_add_to_files,
                            R.id.action_delete -> menuItem.isVisible = false
                        }
                    }
                } else {
                    menu.forEach { menuItem -> menuItem.isVisible = true }
                }

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem?.itemId) {
                        R.id.action_rename -> {
                            ifNonNull(roomNote.name, roomNote.id) { name, id ->
                                activity?.alert(R.string.room_note_rename_dialog_title, withTextInput = true) {
                                    inputText(name)
                                    positiveButton(R.string.room_note_rename_dialog_action_positive) {
                                        viewModel.renameNote(id, inputText.text.toString())
                                    }
                                    negativeButton(R.string.room_note_rename_dialog_action_negative)
                                }?.show()
                            }
                        }
                        R.id.action_download -> {
                            viewModel.downloadNote(roomNote)
                        }
                        R.id.action_add_to_files -> {
                            viewModel.addNoteToFiles(roomNote)
                        }
                        R.id.action_delete -> {
                            activity?.alert(
                                R.string.room_note_delete_dialog_title,
                                R.string.room_note_delete_dialog_message
                            ) {
                                positiveButton(R.string.room_note_delete_dialog_action_positive) {
                                    viewModel.deleteNote(roomNote)
                                }
                                negativeButton(R.string.room_note_delete_dialog_action_negative)
                            }?.show()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                show()
            }
        }

    private val onNoteClick: (view: View, roomNote: KmeRoomNote) -> Unit =
        { view: View, roomNote: KmeRoomNote -> viewModel.openNote(roomNote) }

    private val isLoadingObserver = Observer<Boolean> {
        progressBar.visibility = if (it) VISIBLE else GONE
    }

    private val roomNotesObserver = Observer<List<KmeRoomNote>> {
        adapter.setNotes(it)
    }

    private val adminControlsObserver = Observer<Boolean> {
        isAdmin = it
        fabCreateNote.visibility = if (it) VISIBLE else GONE
    }

    private val addToFilesObserver = Observer<Boolean> {
        showMessage(if (it) R.string.room_note_add_to_files_state_success else R.string.room_note_add_to_files_state_error)
    }

    private val noteDownloadObserver = Observer<Boolean> {
        showMessage(if (it) R.string.room_note_download_state_success else R.string.room_note_download_state_error)
    }

    private val deleteStateObserver = Observer<Boolean> {
        showMessage(if (it) R.string.room_note_delete_state_success else R.string.room_note_delete_state_error)
    }

    private fun showMessage(resId: Int) {
        Snackbar.make(
            noteListRoot,
            resId,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = RoomNoteListFragment()
    }

}
