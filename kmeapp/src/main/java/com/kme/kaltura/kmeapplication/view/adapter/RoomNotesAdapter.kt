package com.kme.kaltura.kmeapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.TimeUtil
import com.kme.kaltura.kmesdk.rest.response.room.notes.KmeRoomNote
import com.stfalcon.chatkit.utils.DateFormatter
import kotlinx.android.synthetic.main.item_list_room_note.view.*
import java.util.*

class RoomNotesAdapter : RecyclerView.Adapter<RoomNotesAdapter.RoomNotesHolder>() {

    private val notesList = mutableListOf<KmeRoomNote>()

    var onNoteClick: ((view: View, roomNote: KmeRoomNote) -> Unit)? = null
    var onNoteActionsClick: ((view: View, roomNote: KmeRoomNote) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomNotesHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_room_note, parent, false)
        return RoomNotesHolder(view)
    }

    override fun onBindViewHolder(holder: RoomNotesHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount() = notesList.size

    fun setNotes(data: List<KmeRoomNote>) {
        notesList.clear()
        notesList.addAll(data)
        notifyDataSetChanged()
    }

    inner class RoomNotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(roomNote: KmeRoomNote) {
            with(itemView) {
                tvNoteName.text = roomNote.name
                roomNote.dateModified?.let {
                    tvNoteDate.text = DateFormatter.format(
                        Date(it * 1000L),
                        TimeUtil.Template.STRING_MONTH_NAME_DAY_YEAR_TIME.get()
                    )
                }

                ivExpandActions.setOnClickListener {
                    onNoteActionsClick?.invoke(itemView, roomNote)
                }
                setOnClickListener {
                    onNoteClick?.invoke(itemView, roomNote)
                }
            }
        }
    }

}
