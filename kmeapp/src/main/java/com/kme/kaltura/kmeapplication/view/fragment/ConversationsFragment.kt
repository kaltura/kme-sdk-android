package com.kme.kaltura.kmeapplication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.kme.kaltura.kmeapplication.data.MappedConversation
import com.kme.kaltura.kmeapplication.util.TimeUtil
import com.kme.kaltura.kmeapplication.util.extensions.glide
import com.kme.kaltura.kmeapplication.util.extensions.ifNonNull
import com.kme.kaltura.kmeapplication.view.activity.RoomActivity
import com.kme.kaltura.kmeapplication.view.adapter.viewholder.ConversationViewHolder
import com.kme.kaltura.kmeapplication.viewmodel.ChatViewModel
import com.kme.kaltura.kmeapplication.viewmodel.ConversationsViewModel
import com.kme.kaltura.kmeapplication.viewmodel.RoomSettingsViewModel
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.stfalcon.chatkit.utils.DateFormatter
import kotlinx.android.synthetic.main.fragment_conversations.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.collections.ArrayList

class ConversationsFragment : Fragment() {

    private val settingsViewModel: RoomSettingsViewModel by sharedViewModel()
    private val chatViewModel: ChatViewModel by sharedViewModel()
    private val conversationViewModel: ConversationsViewModel by sharedViewModel()

    private val companyId: Long? by lazy { arguments?.getLong(COMPANY_ID_EXTRA, 0L) }
    private val roomId: Long? by lazy { arguments?.getLong(ROOM_ID_EXTRA, 0L) }

    private val imageLoader: ImageLoader by lazy {
        ImageLoader { imageView, url, _ ->
            if (url != null) {
                imageView.glide(url) {
                    circleCrop()
                }
            }
        }
    }

    private lateinit var conversations: List<MappedConversation>
    private lateinit var adapter: DialogsListAdapter<MappedConversation>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_conversations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conversations = arguments?.getParcelableArrayList(CONVERSATIONS_LIST_ARG) ?: arrayListOf()

        setupList()
        setupViewModel()
    }

    private fun setupList() {
        adapter = DialogsListAdapter(
            R.layout.item_conversation_layout,
            ConversationViewHolder::class.java,
            imageLoader
        )

        adapter.setDatesFormatter {
            when {
                DateFormatter.isToday(it) -> DateFormatter.format(it, TimeUtil.Template.TIME.get())
                    .toUpperCase(Locale.getDefault())
                DateFormatter.isYesterday(it) -> getString(R.string.yesterday)
                else -> DateFormatter.format(it, TimeUtil.Template.STRING_DAY_MONTH_YEAR.get())
            }
        }

        adapter.setOnDialogClickListener {
            conversationViewModel.openConversation(it)
        }

        rvConversations.setAdapter(adapter)
    }

    private fun setupViewModel() {
        settingsViewModel.moderatorLiveData.observe(viewLifecycleOwner, moderatorObserver)

        chatViewModel.newMessageLiveData.observe(viewLifecycleOwner, newMessageObserver)
        chatViewModel.updateMessageLiveData.observe(viewLifecycleOwner, updateMessageObserver)

        conversationViewModel.conversationsLiveData.observe(
            viewLifecycleOwner,
            conversationsChangedObserver
        )
        conversationViewModel.conversationChangedLiveData.observe(
            viewLifecycleOwner,
            conversationChangedObserver
        )

        ifNonNull(companyId, roomId) { companyId, roomId ->
            conversationViewModel.loadConversations(roomId, companyId)
        }
    }

    private val moderatorObserver = Observer<Pair<Long, Boolean>> {
        conversationViewModel.filter()
    }

    private val conversationsChangedObserver = Observer<List<MappedConversation>> {
        if (::adapter.isInitialized) {
            conversations = it
            adapter.setItems(it)
        }
    }

    private val conversationChangedObserver = Observer<MappedConversation> {
        if (::adapter.isInitialized) {
            adapter.updateItemById(it)
        }
    }

    private val newMessageObserver = Observer<MappedChatMessage> {
        conversationViewModel.onNewMessage(it, true)
    }

    private val updateMessageObserver = Observer<Pair<String, MappedChatMessage>> {
        conversationViewModel.onNewMessage(it.second, false)
    }

    companion object {
        const val CONVERSATIONS_LIST_ARG = "CONVERSATIONS_LIST_ARG"
        const val COMPANY_ID_EXTRA = "COMPANY_ID_EXTRA"
        const val ROOM_ID_EXTRA = "ROOM_ID_EXTRA"

        @JvmStatic
        fun newInstance(
            companyId: Long,
            roomId: Long,
            conversations: List<MappedConversation>
        ) = ConversationsFragment().apply {
            arguments = Bundle().apply {
                putLong(RoomActivity.COMPANY_ID_EXTRA, companyId)
                putLong(RoomActivity.ROOM_ID_EXTRA, roomId)
                putParcelableArrayList(CONVERSATIONS_LIST_ARG, ArrayList(conversations))
            }
        }
    }

}