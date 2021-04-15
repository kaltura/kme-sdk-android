package com.kme.kaltura.kmeapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.invisible
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmePlatformType
import kotlinx.android.synthetic.main.layout_view_renderer_desktop.view.*
import kotlinx.android.synthetic.main.layout_view_renderer_mobile.view.*
import kotlinx.android.synthetic.main.layout_view_renderer_mobile.view.ivMute
import kotlinx.android.synthetic.main.layout_view_renderer_mobile.view.ivRaiseHand
import kotlinx.android.synthetic.main.layout_view_renderer_mobile.view.rendererContainer
import kotlinx.android.synthetic.main.layout_view_renderer_mobile.view.speakingIndicator
import kotlinx.android.synthetic.main.layout_view_renderer_mobile.view.tvUserName

class RenderersAdapter(
    private val width: Int,
    private val height: Int
) : RecyclerView.Adapter<RenderersAdapter.RendererHolder>() {

    private var isModerator = false
    private val participants = mutableListOf<KmeParticipant>()
    private val renderers = mutableListOf<KmeSurfaceRendererView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RendererHolder {
        val layoutRes = when (viewType) {
            TYPE_DESKTOP -> R.layout.layout_view_renderer_desktop
            TYPE_MOBILE -> R.layout.layout_view_renderer_mobile
            else -> R.layout.layout_view_renderer_desktop
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutRes, parent, false)

        view.layoutParams.apply {
            width = this@RenderersAdapter.width
            height = this@RenderersAdapter.height
        }

        return RendererHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when (participants[position].deviceType) {
            KmePlatformType.DESKTOP -> TYPE_DESKTOP
            KmePlatformType.MOBILE -> TYPE_MOBILE
            else -> TYPE_DESKTOP
        }
    }

    override fun onBindViewHolder(
        holder: RendererHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (payloads.contains(BACKGROUND_PAYLOAD)) {
                holder.updateBackground(participants[position])
            }
            if (payloads.contains(AUDIO_STATE_PAYLOAD)) {
                holder.updateAudioState(participants[position])
            }
            if (payloads.contains(VIDEO_STATE_PAYLOAD)) {
                holder.updateVideoState(participants[position])
            }
            if (payloads.contains(HAND_RAISE_PAYLOAD)) {
                holder.updateHandRaiseState(participants[position])
            }
        }
    }

    override fun onBindViewHolder(holder: RendererHolder, position: Int) {
        holder.bind(participants[position], renderers[position])
    }

    override fun getItemCount() = participants.size

    fun setIsModerator(isModerator: Boolean) {
        this.isModerator = isModerator
        participants.forEachIndexed {index, _ ->
            notifyItemChanged(index, HAND_RAISE_PAYLOAD)
        }
    }

    fun addRenderer(participant: KmeParticipant, renderer: KmeSurfaceRendererView) {
        participants.add(participant)
        renderers.add(renderer)
        notifyItemInserted(participants.size - 1)
    }

    fun removeRendererFor(userId: Long) {
        participants.find { target -> target.userId == userId }
            ?.let {
                val indexOf = participants.indexOf(it)
                participants.removeAt(indexOf)
                renderers.removeAt(indexOf)
                notifyItemRemoved(indexOf)
            }
    }

    fun updateSpeakingStateFor(participant: KmeParticipant) {
        participants.find { target -> target.userId == participant.userId }
            ?.let {
                val indexOf = participants.indexOf(it)
                participants[indexOf] = it
                notifyItemChanged(indexOf, BACKGROUND_PAYLOAD)
            }
    }

    fun updateAudioStateFor(participant: KmeParticipant) {
        participants.find { target -> target.userId == participant.userId }
            ?.let {
                val indexOf = participants.indexOf(it)
                participants[indexOf] = it
                notifyItemChanged(indexOf, AUDIO_STATE_PAYLOAD)
                notifyItemChanged(indexOf, BACKGROUND_PAYLOAD)
            }
    }

    fun updateAudioStateFor(participantsToUpdate: List<KmeParticipant>) {
        participantsToUpdate.forEach { toUpdate ->
            participants.find { target -> target.userId == toUpdate.userId }
                ?.let {
                    it.micState = toUpdate.micState
                    val indexOf = participants.indexOf(it)
                    participants[indexOf] = it
                    notifyItemChanged(indexOf, AUDIO_STATE_PAYLOAD)
                    notifyItemChanged(indexOf, BACKGROUND_PAYLOAD)
                }
        }
    }

    fun updateVideoStateFor(participant: KmeParticipant) {
        participants.find { target -> target.userId == participant.userId }
            ?.let {
                val indexOf = participants.indexOf(it)
                participants[indexOf] = it
                notifyItemChanged(indexOf, VIDEO_STATE_PAYLOAD)
            }
    }

    fun updateVideoStateFor(participantsToUpdate: List<KmeParticipant>) {
        participantsToUpdate.forEach { toUpdate ->
            participants.find { target -> target.userId == toUpdate.userId }
                ?.let {
                    it.webcamState = toUpdate.webcamState
                    val indexOf = participants.indexOf(it)
                    participants[indexOf] = it
                    notifyItemChanged(indexOf, VIDEO_STATE_PAYLOAD)
                }
        }
    }

    fun updateRaiseHandFor(participant: KmeParticipant) {
        participants.find { target -> target.userId == participant.userId }
            ?.let {
                val indexOf = participants.indexOf(it)
                participants[indexOf] = it
                notifyItemChanged(indexOf, HAND_RAISE_PAYLOAD)
            }
    }

    inner class RendererHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(participant: KmeParticipant, renderer: KmeSurfaceRendererView?) {
            with(itemView) {
                tvUserName.text = participant.fullName

                renderer?.let {
                    if (it.parent != null) {
                        (it.parent as ViewGroup).removeView(it)
                    }
                    rendererContainer.addView(it)
                }

                updateBackground(participant)
                updateAudioState(participant)
                updateVideoState(participant)
                updateHandRaiseState(participant)
            }
        }

        fun updateBackground(participant: KmeParticipant) {
            with(itemView) {
                speakingIndicator.visibility = if (participant.isSpeaking) VISIBLE else GONE
            }
        }

        fun updateAudioState(participant: KmeParticipant) {
            with(itemView) {
                if (participant.micState == KmeMediaDeviceState.LIVE) {
                    ivMute.gone()
                } else {
                    ivMute.visible()
                }
            }
        }

        fun updateVideoState(participant: KmeParticipant) {
            with(itemView) {
                if (participant.webcamState == KmeMediaDeviceState.LIVE) {
                    rendererContainer.visible()
                } else {
                    rendererContainer.invisible()
                }
            }
        }

        fun updateHandRaiseState(participant: KmeParticipant) {
            with(itemView) {
                if (isModerator && (participant.isHandRaised || participant.timeHandRaised != 0L)) {
                    ivRaiseHand.visible()
                } else {
                    ivRaiseHand.invisible()
                }
            }
        }
    }

    companion object Payloads {

        const val BACKGROUND_PAYLOAD = "BACKGROUND_PAYLOAD"
        const val AUDIO_STATE_PAYLOAD = "AUDIO_STATE_PAYLOAD"
        const val VIDEO_STATE_PAYLOAD = "VIDEO_STATE_PAYLOAD"
        const val HAND_RAISE_PAYLOAD = "HAND_RAISE_PAYLOAD"

        private const val TYPE_DESKTOP = 1
        private const val TYPE_MOBILE = 2

    }

}
