package com.kme.kaltura.kmeapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.glide
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmePlatformType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserRole
import kotlinx.android.synthetic.main.item_participant_layout.view.*

class ParticipantsAdapter : RecyclerView.Adapter<ParticipantsAdapter.ParticipantHolder>(),
    Filterable {

    private val participantsList = mutableListOf<KmeParticipant>()
    private val searchableList = mutableListOf<KmeParticipant>()

    var onParticipantClick: ((view: View, participant: KmeParticipant) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant_layout, parent, false)
        return ParticipantHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantHolder, position: Int) {
        holder.bind(searchableList[position])
    }

    override fun getItemCount() = searchableList.size

    fun setParticipants(data: List<KmeParticipant>) {
        participantsList.clear()
        participantsList.addAll(data)
        searchableList.clear()
        searchableList.addAll(participantsList)
        notifyDataSetChanged()
    }

    fun addOrUpdateParticipant(participant: KmeParticipant) {
        val indexOf =
            participantsList.indexOfFirst { localParticipant -> localParticipant.userId == participant.userId }

        if (indexOf >= 0) {
            participantsList[indexOf] = participant
        } else {
            participantsList.add(0, participant)
        }

        val searchableIndexOf =
            searchableList.indexOfFirst { localParticipant -> localParticipant.userId == participant.userId }

        if (searchableIndexOf >= 0) {
            searchableList[searchableIndexOf] = participant
            notifyItemChanged(searchableIndexOf)
        } else {
            filter.filter(filter.constraint)
        }
    }

    fun removeParticipant(participantId: Long) {
        val indexOf =
            participantsList.indexOfFirst { localParticipant -> localParticipant.userId == participantId }

        if (indexOf >= 0) {
            participantsList.removeAt(indexOf)
        }

        val searchableIndexOf =
            searchableList.indexOfFirst { localParticipant -> localParticipant.userId == participantId }

        if (searchableIndexOf >= 0) {
            searchableList.removeAt(searchableIndexOf)
            notifyItemRemoved(searchableIndexOf)
        }
    }

    inner class ParticipantHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(participant: KmeParticipant) {
            with(itemView) {
                tvFullName.text = participant.fullName
                ivPlatformType.setImageResource(
                    if (KmePlatformType.MOBILE == participant.deviceType)
                        R.drawable.ic_mobile
                    else
                        R.drawable.ic_desktop
                )

                when (participant.timeHandRaised != 0L) {
                    true -> {
                        ivRaiseHand.visible()
                        ivRaiseHand.setImageResource(R.drawable.ic_room_raise_hand_off)
                    }
                    else -> {
                        ivRaiseHand.setImageResource(0)
                        ivRaiseHand.gone()
                    }
                }

                when {
                    participant.isCaptioner == true -> {
                        ivUserRole.visible()
                        ivUserRole.setImageResource(R.drawable.ic_captioner)
                    }
                    participant.userRole == KmeUserRole.INSTRUCTOR ||
                            participant.isModerator == true -> {
                        ivUserRole.visible()
                        ivUserRole.setImageResource(R.drawable.ic_moderator)
                    }
                    else -> {
                        ivUserRole.gone()
                    }
                }

                when (participant.webcamState) {
                    KmeMediaDeviceState.LIVE -> {
                        ivWebcamState.setBackgroundResource(0)
                        ivWebcamState.gone()
                    }
                    else -> {
                        ivWebcamState.visible()
                        ivWebcamState.setBackgroundResource(R.drawable.ic_webcam_off)
                    }
                }

                when (participant.micState) {
                    KmeMediaDeviceState.LIVE -> {
                        ivMicState.setBackgroundResource(0)
                        ivMicState.gone()
                    }
                    else -> {
                        ivMicState.visible()
                        ivMicState.setBackgroundResource(R.drawable.ic_mic_off)
                    }
                }

                ivAvatar.glide(participant.avatar) {
                    circleCrop()
                }

                setOnClickListener {
                    onParticipantClick?.invoke(itemView, participant)
                }
            }
        }
    }

    private val filter by lazy {
        object : Filter() {
            var constraint: CharSequence? = null
                private set

            private val filterResults = FilterResults()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                this.constraint = constraint
                searchableList.clear()
                if (constraint.isNullOrBlank()) {
                    searchableList.addAll(participantsList)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                    for (item in 0..participantsList.size) {
                        if (participantsList[item].fullName?.toLowerCase()
                                ?.contains(filterPattern) == true
                        ) {
                            searchableList.add(participantsList[item])
                        }
                    }
                }
                return filterResults.also {
                    it.values = searchableList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter {
        return filter
    }
}