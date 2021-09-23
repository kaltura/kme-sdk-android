package com.kme.kaltura.kmeapplication.view.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.databinding.LayoutViewRendererBinding
import com.kme.kaltura.kmeapplication.util.extensions.glide
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.invisible
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmesdk.isLandscape
import com.kme.kaltura.kmesdk.setVisibility
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.KmePlatformType
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType

class RenderersAdapter(
    resources: Resources,
    val onViewBind: ((holder: KmeSurfaceRendererView, participantId: Long?) -> Unit)? = null
) : RecyclerView.Adapter<RenderersAdapter.GalleryViewHolder>() {

    private val participants = mutableListOf<KmeParticipant>()

    private val centerCrop by lazy { CenterCrop() }

    private val roundedCorners: RoundedCorners by lazy {
        RoundedCorners(resources.getDimensionPixelOffset(R.dimen.default_avatar_radius))
    }

    private var isModerator = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = LayoutViewRendererBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GalleryViewHolder(binding, parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return when (participants[position].deviceType) {
            KmePlatformType.DESKTOP -> TYPE_DESKTOP
            KmePlatformType.MOBILE -> TYPE_MOBILE
            else -> TYPE_DESKTOP
        }
    }

    override fun onBindViewHolder(
        holder: GalleryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (payloads.contains(RENDER_SIZE_PAYLOAD)) {
                holder.updateSize()
            }
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

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val participant = participants[position]
        onViewBind?.invoke(holder.binding.renderer, participant.userId)
        holder.bind(participant)
    }

    override fun getItemCount() = participants.size

    fun setIsModerator(isModerator: Boolean) {
        this.isModerator = isModerator
        participants.forEachIndexed { index, _ ->
            notifyItemChanged(index, HAND_RAISE_PAYLOAD)
        }
    }

    fun addRenderer(participant: KmeParticipant) {
        if (!participants.contains(participant)) {
            participants.add(participant)
            notifyItemInserted(participants.size - 1)
            notifyItemRangeChanged(0, itemCount, RENDER_SIZE_PAYLOAD)
        }
    }

    fun removeRendererFor(userId: Long) {
        participants.find { target ->
            target.userId == userId
        }?.let {
            val indexOf = participants.indexOf(it)
            participants.removeAt(indexOf)
            notifyItemRemoved(indexOf)
            notifyItemRangeChanged(0, itemCount, RENDER_SIZE_PAYLOAD)
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
        participants.find { target ->
            target.userId == participant.userId
        }?.let {
            val indexOf = participants.indexOf(it)
            participants[indexOf] = it
            notifyItemChanged(indexOf, AUDIO_STATE_PAYLOAD)
            notifyItemChanged(indexOf, BACKGROUND_PAYLOAD)
        }
    }

    fun updateAudioStateFor(participantsToUpdate: List<KmeParticipant>) {
        participantsToUpdate.forEach { toUpdate ->
            participants.find { target ->
                target.userId == toUpdate.userId
            }?.let {
                it.micState = toUpdate.micState
                val indexOf = participants.indexOf(it)
                participants[indexOf] = it
                notifyItemChanged(indexOf, AUDIO_STATE_PAYLOAD)
                notifyItemChanged(indexOf, BACKGROUND_PAYLOAD)
            }
        }
    }

    fun updateVideoStateFor(participant: KmeParticipant) {
        participants.find { target ->
            target.userId == participant.userId
        }?.let {
            val indexOf = participants.indexOf(it)
            participants[indexOf] = it
            notifyItemChanged(indexOf, VIDEO_STATE_PAYLOAD)
        }
    }

    fun updateVideoStateFor(participantsToUpdate: List<KmeParticipant>) {
        participantsToUpdate.forEach { toUpdate ->
            participants.find { target ->
                target.userId == toUpdate.userId
            }?.let {
                it.webcamState = toUpdate.webcamState
                val indexOf = participants.indexOf(it)
                participants[indexOf] = it
                notifyItemChanged(indexOf, VIDEO_STATE_PAYLOAD)
            }
        }
    }

    fun updateRaiseHandFor(participant: KmeParticipant) {
        participants.find { target ->
            target.userId == participant.userId
        }?.let {
            val indexOf = participants.indexOf(it)
            participants[indexOf] = it
            notifyItemChanged(indexOf, HAND_RAISE_PAYLOAD)
        }
    }

    inner class GalleryViewHolder(
        val binding: LayoutViewRendererBinding,
        private val parent: ViewGroup,
        private val viewType: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: KmeParticipant) {
            with(binding) {
                updateSize()

                val params = renderer.layoutParams as FrameLayout.LayoutParams
                if (viewType == TYPE_DESKTOP) {
                    params.width = FrameLayout.LayoutParams.MATCH_PARENT
                    params.height = FrameLayout.LayoutParams.WRAP_CONTENT
                } else {
                    params.width = FrameLayout.LayoutParams.WRAP_CONTENT
                    params.height = FrameLayout.LayoutParams.MATCH_PARENT
                }
                renderer.layoutParams = params

                tvUserName.text = participant.fullName

                updateBackground(participant)
                updateAudioState(participant)
                updateVideoState(participant)
                updateHandRaiseState(participant)
            }
        }

        fun updateSize() {
            with(binding) {
                val params = root.layoutParams as StaggeredGridLayoutManager.LayoutParams
                params.isFullSpan = false

                val pages = if (itemCount % 4 == 0) {
                    itemCount / 4
                } else {
                    itemCount / 4 + 1
                }

                if (adapterPosition < (pages - 1) * 4) {
                    params.width = parent.width / 2
                    params.height = parent.height / 2
                } else {
                    when (itemCount % 4) {
                        0 -> {
                            params.width = parent.width / 2
                            params.height = parent.height / 2
                        }
                        1 -> {
                            params.isFullSpan = true
                            params.width = parent.width
                            params.height = parent.height
                        }
                        2 -> {
                            if (root.context.isLandscape()) {
                                params.isFullSpan = true
                                params.width = parent.width / 2
                                params.height = parent.height
                            } else {
                                params.width = parent.width
                                params.height = parent.height / 2
                            }
                        }
                        3 -> {
                            if (root.context.isLandscape()) {
                                if (adapterPosition < 2) {
                                    params.width = parent.width / 2
                                    params.height = parent.height / 2
                                } else {
                                    params.width = parent.width / 2
                                    params.height = parent.height
                                }
                            } else {
                                if (adapterPosition == itemCount - 2) {
                                    params.width = parent.width
                                    params.height = parent.height / 2
                                } else {
                                    params.width = parent.width / 2
                                    params.height = parent.height / 2
                                }
                            }
                        }
                    }
                }
            }
        }

        fun updateBackground(participant: KmeParticipant) {
            with(binding) {
                speakingIndicator.visibility =
                    if (participant.isSpeaking) View.VISIBLE else View.GONE
            }
        }

        fun updateAudioState(participant: KmeParticipant) {
            with(binding) {
                if (participant.micState == KmeMediaDeviceState.LIVE) {
                    ivMute.gone()
                } else {
                    ivMute.visible()
                }
            }
        }

        fun updateVideoState(participant: KmeParticipant) {
            toggleRendererContainer(
                participant,
                participant.webcamState == KmeMediaDeviceState.LIVE
            )
        }

        fun updateHandRaiseState(participant: KmeParticipant) {
            with(binding) {
                if (isModerator && participant.timeHandRaised != 0L) {
                    ivRaiseHand.visible()
                } else {
                    ivRaiseHand.invisible()
                }
            }
        }

        private fun toggleRendererContainer(participant: KmeParticipant, showRenderView: Boolean) {
            with(binding) {
                if (showRenderView) {
                    ivAvatar.gone()
                    initialsContainer.gone()
                    renderer.visible()
                } else {
                    renderer.invisible()
                    if (participant.isDynamicAvatar() || participant.avatar.isNullOrEmpty()) {
                        val isDailIn = participant.userType == KmeUserType.DIAL
                        ivAvatar.gone()
                        tvInitials.text = participant.fullName?.get(0)?.toString() ?: ""
                        tvInitials.setVisibility(!isDailIn)
                        ivDailIn.setVisibility(isDailIn)
                        initialsContainer.visible()
                    } else {
                        initialsContainer.gone()
                        ivAvatar.visible()
                        ivAvatar.glide(participant.avatar, 0) {
                            transform(centerCrop, roundedCorners)
                        }
                    }
                }
            }
        }
    }

    companion object Payloads {

        const val BACKGROUND_PAYLOAD = "BACKGROUND_PAYLOAD"
        const val AUDIO_STATE_PAYLOAD = "AUDIO_STATE_PAYLOAD"
        const val VIDEO_STATE_PAYLOAD = "VIDEO_STATE_PAYLOAD"
        const val HAND_RAISE_PAYLOAD = "HAND_RAISE_PAYLOAD"
        const val RENDER_SIZE_PAYLOAD = "RENDER_SIZE_PAYLOAD"

        private const val TYPE_DESKTOP = 1
        private const val TYPE_MOBILE = 2

    }

}
