package com.kme.kaltura.kmeapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.glide
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmesdk.rest.response.room.KmeBaseRoom
import kotlinx.android.synthetic.main.item_room_layout.view.*

class RoomsListAdapter : RecyclerView.Adapter<RoomsListAdapter.RoomHolder>() {

    private val rooms = mutableListOf<KmeBaseRoom>()

    var onRoomClick: ((room: KmeBaseRoom) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_room_layout, parent, false)
        return RoomHolder(view)
    }

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount() = rooms.size

    fun addData(data: List<KmeBaseRoom>) {
        rooms.clear()
        rooms.addAll(data)
        notifyDataSetChanged()
    }

    inner class RoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(room: KmeBaseRoom) {
            with(itemView) {
                tvRoomTitle.text = room.name.toNonNull()
                tvRoomSummary.text = room.summary.toNonNull()
                ivRoomAvatar.glide(room.avatar)

                setOnClickListener {
                    onRoomClick?.invoke(room)
                }
            }
        }
    }
}