package com.kme.kaltura.kmeapplication.view.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.data.MappedConversation
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.popBackStack
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.view.IBottomSheetCallback
import com.kme.kaltura.kmeapplication.view.adapter.viewholder.CustomIncomingTextMessageViewHolder
import com.kme.kaltura.kmeapplication.view.adapter.viewholder.CustomOutcomingTextMessageViewHolder
import com.kme.kaltura.kmeapplication.view.adapter.viewholder.OnChatContextMenuListener
import com.kme.kaltura.kmeapplication.viewmodel.ChatViewModel
import com.kme.kaltura.kmeapplication.viewmodel.ConversationsViewModel
import com.kme.kaltura.kmeapplication.viewmodel.ParticipantsViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomStateViewModel
import com.kme.kaltura.kmesdk.ws.message.participant.KmeParticipant
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.chatkit.utils.DateFormatter
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.item_reply_text_message.view.*
import kotlinx.android.synthetic.main.layout_room_bottom_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChatFragment : KmeFragment(), MessagesListAdapter.OnLoadMoreListener,
    OnChatContextMenuListener, IBottomSheetCallback {

    private val roomViewModel: RoomStateViewModel by sharedViewModel()
    private val participantsViewModel: ParticipantsViewModel by sharedViewModel()
    private val conversationViewModel: ConversationsViewModel by sharedViewModel()
    private val viewModel: ChatViewModel by sharedViewModel()

    private val companyId: Long by lazy { arguments?.getLong(COMPANY_ID_ARG, 0) ?: 0 }

    private val roomId: Long by lazy { arguments?.getLong(ROOM_ID_ARG, 0) ?: 0 }

    private val conversation: MappedConversation? by lazy {
        arguments?.getParcelable(CONVERSATION_ARG)
    }

    private lateinit var adapter: MessagesListAdapter<MappedChatMessage>

    private var replyMessage: MappedChatMessage? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
        conversationViewModel.setOpenedConversation(conversation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupMessagesList()
        setupInputView()
        setupViewModel()
    }

    private fun setupToolbar() {
        activity?.sheetActionBarContainer.visible()
        activity?.tvSheetBarTitle?.text = conversation?.dialogName ?: getString(R.string.chat)
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

    private fun removeFragment() {
        viewModel.onConversationClosed()
        (activity as AppCompatActivity).popBackStack()
    }

    private fun setupInputView() {
        input.setInputListener { input ->
            val inputText = input.trim()
            if (inputText.isNotBlank()) {
                conversation?.let { conversation ->
                    replyMessageInput.gone()
                    adapter.addToStart(
                        viewModel.buildSelfMessage(
                            conversation.id,
                            inputText,
                            replyMessage
                        ), true
                    )
                    viewModel.send(
                        conversation.id.toNonNull(),
                        roomId,
                        companyId,
                        inputText,
                        replyMessage
                    )
                    replyMessage = null
                    return@setInputListener true
                }
            }
            false
        }
    }

    private fun setupMessagesList() {
        val outcomingPayload = CustomOutcomingTextMessageViewHolder.Payload()
            .apply {
                chatType = conversation?.conversationType
                contextMenuListener = this@ChatFragment
            }
        val incomingPayload = CustomIncomingTextMessageViewHolder.Payload()
            .apply {
                chatType = conversation?.conversationType
                contextMenuListener = this@ChatFragment
            }

        val holdersConfig = MessageHolders()
            .setIncomingTextConfig(
                CustomIncomingTextMessageViewHolder::class.java,
                R.layout.item_custom_incoming_text_message,
                incomingPayload
            )
            .setOutcomingTextConfig(
                CustomOutcomingTextMessageViewHolder::class.java,
                R.layout.item_custom_outcoming_text_message,
                outcomingPayload
            )

        adapter = MessagesListAdapter<MappedChatMessage>(
            viewModel.getCurrentUserId().toString(),
            holdersConfig,
            null
        ).apply {
            setLoadMoreListener(this@ChatFragment)
        }

        messagesList.setAdapter(adapter)
    }

    private fun setupViewModel() {
        viewModel.messagesUpdatesLiveData.observe(viewLifecycleOwner, messagesObserver)
        viewModel.newMessageLiveData.observe(viewLifecycleOwner, newMessageObserver)
        viewModel.updateMessageLiveData.observe(viewLifecycleOwner, updateMessageObserver)
        viewModel.deleteMessageLiveData.observe(viewLifecycleOwner, deleteMessageObserver)

        viewModel.loadMessages(conversation?.id.toNonNull(), roomId, companyId)
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        if (viewModel.allowLoadMore) {
            viewModel.getLastLoadedMessage()?.let {
                viewModel.loadMessages(conversation?.id.toNonNull(), roomId, companyId, it.id)
            }
        }
    }

    override fun isParticipantJoined(userId: Long) =
        participantsViewModel.isParticipantJoined(userId)

    override fun getCurrentParticipant(): KmeParticipant? = viewModel.currentParticipant

    override fun onStartPrivateChat(userId: Long) {
        viewModel.startPrivateChat(userId, roomId, companyId)
    }

    override fun onDelete(message: MappedChatMessage) {
        conversation?.let {
            viewModel.deleteMessage(message.id, it.id.toNonNull(), roomId, companyId)
        }
    }

    override fun onReply(message: MappedChatMessage, replyAll: Boolean) {
        this.replyMessage = message
        showReplyInput(message)
    }

    private fun showReplyInput(message: MappedChatMessage) {
        replyMessageInput.visible()
        with(replyMessageInput) {
            replyUserName.text = message.user.name
            replyMessageText.text = message.text
            replyMessageTime.text =
                DateFormatter.format(message.createdAt, DateFormatter.Template.TIME)
            cancelReplyMessage.visible()
            cancelReplyMessage.setOnClickListener {
                replyMessage = null
                replyMessageInput.gone()
                it.setOnClickListener(null)
            }
        }
    }

    private val messagesObserver = Observer<List<MappedChatMessage>> {
        adapter.addToEnd(it, false)
    }

    private val newMessageObserver = Observer<MappedChatMessage> {
        if (it.conversationId == conversation?.id) {
            adapter.addToStart(it, true)
        }
    }

    private val updateMessageObserver = Observer<Pair<String, MappedChatMessage>> {
        if (it.second.conversationId == conversation?.id) {
            adapter.update(it.first, it.second)
        }
    }

    private val deleteMessageObserver = Observer<String> {
        adapter.deleteById(it)
    }

    override fun onBottomSheetOpened() {
        conversationViewModel.setOpenedConversation(conversation)
    }

    override fun onBottomSheetClosed() {
        conversationViewModel.setOpenedConversation(null)
        viewModel.onConversationClosed()
    }

    override fun onDestroyView() {
        activity?.sheetActionBarContainer.gone()
        conversationViewModel.setOpenedConversation(null)
        viewModel.onConversationClosed()
        super.onDestroyView()
    }

    companion object {
        const val CONVERSATION_ARG = "CONVERSATION_ARG"
        const val ROOM_ID_ARG = "ROOM_ID_ARG"
        const val COMPANY_ID_ARG = "COMPANY_ID_ARG"

        @JvmStatic
        fun newInstance(
            conversation: MappedConversation,
            roomId: Long,
            companyId: Long,
        ) = ChatFragment().apply {
            arguments = Bundle().apply {
                putParcelable(CONVERSATION_ARG, conversation)
                putLong(ROOM_ID_ARG, roomId)
                putLong(COMPANY_ID_ARG, companyId)
            }
        }
    }

}