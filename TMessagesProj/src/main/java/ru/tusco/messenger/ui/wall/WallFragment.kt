package ru.tusco.messenger.ui.wall

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
import org.telegram.ui.ActionBar.BackDrawable
import org.telegram.ui.Cells.ChatLoadingCell
import org.telegram.ui.Cells.ChatMessageCell
import org.telegram.ui.ChatActivity
import ru.tusco.messenger.settings.WallSettingsActivity
import ru.tusco.messenger.ui.mvvm.ViewModelFactory
import kotlin.math.max

class WallFragment : ChatActivity(Bundle().also { it.putInt("chatMode", MODE_DAHL_WALL) }) {

    companion object {
        const val MENU_ITEM_UPDATE = 100000
        const val MENU_ITEM_OTHER_OPTIONS = 100001
        const val MENU_ITEM_SETTINGS = 100002

    }

    protected lateinit var viewModel: WallViewModel

    private val stateObserver = { state: WallState -> renderState(state) }

    private var scrollToTop = true

    private var listState: WallState = WallState.Initial

    override fun onFragmentCreate(): Boolean {
        super.onFragmentCreate()
        viewModel = ViewModelFactory.getViewModel(WallViewModel::class.java)
        viewModel.reload()
        return true
    }

    override fun onFragmentDestroy() {
        ViewModelFactory.destroyViewModel(WallViewModel::class.java)
        super.onFragmentDestroy()
    }

    override fun createView(context: Context): View {
        val view = super.createView(context)
        chatAdapter = createAdapter()
        chatListView.emptyView = null
        chatListView.adapter = chatAdapter

        chatActivityEnterView.visibility = View.GONE

        actionBar.backButtonDrawable = BackDrawable(false)
        actionBar.setAllowOverlayTitle(true)
        actionBar.setTitle(LocaleController.getString(R.string.Wall))
        actionBar.menu.clearItems()

        actionBar.createMenu().apply {
            addItem(MENU_ITEM_UPDATE, R.drawable.refresh_outline_28).contentDescription = LocaleController.getString(R.string.Update)
            addItem(MENU_ITEM_OTHER_OPTIONS, R.drawable.ic_ab_other, null).apply {
                contentDescription = LocaleController.getString(R.string.AccDescrMoreOptions)
                lazilyAddSubItem(MENU_ITEM_SETTINGS, R.drawable.msg_settings, LocaleController.getString(R.string.WallSettings))
            }
        }
        val parentMenuOnItemClick = actionBar.actionBarMenuOnItemClick
        actionBar.setActionBarMenuOnItemClick(object : ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                when (id) {
                    MENU_ITEM_UPDATE -> {
                        viewModel.reload()
                    }

                    MENU_ITEM_SETTINGS -> presentFragment(WallSettingsActivity())

                    else -> parentMenuOnItemClick?.onItemClick(id)
                }
            }
        })

        chatListView?.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(isDahlWallNextPageLoading && chatAdapter!!.loadingDownRow < 0){
                    chatAdapter?.updateRowsSafe()
                }
            }
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        avatarContainer?.visibility = View.INVISIBLE
        chatActivityEnterView.visibility = View.GONE
        viewModel.state.observe(stateObserver)
    }

    override fun onPause() {
        viewModel.state.removeObserver(stateObserver)
        super.onPause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderState(state: WallState) {
        FileLog.d("WallFragment, renderState, state: $state")
        isDahlWallNextPageLoading = state is WallState.LoadingNextPage
        when (state) {
            is WallState.Loading -> {
                scrollToTop = true
                clearChatData(false)
                showPageDownButton(false)
                emptyViewContainer?.visibility = View.GONE
                chatListView?.emptyView = null
            }
            is WallState.Empty -> {
                scrollToTop = true
                clearChatData(false)
                showPageDownButton(false)
                chatListView?.emptyView = emptyViewContainer
                emptyViewContainer?.visibility = View.VISIBLE
            }
            is WallState.NewData -> {
                processNewMessages(ArrayList(state.data), false)
                if(state.data.isNotEmpty()){
                    if(scrollToTop){
                        scrollToTop = false
                        chatListView?.scrollToPosition(chatAdapter.itemCount - 1)
                    }else {
                        showPageDownButton(true)
                    }
                }else{
                    chatAdapter?.updateRowsSafe()
                }
                emptyViewContainer?.visibility = View.GONE
                chatListView?.emptyView = null
            }
            is WallState.LoadingNextPage -> {
                emptyViewContainer?.visibility = View.GONE
                chatListView?.emptyView = null
            }

            else -> {}
        }
        this.listState = state
        setUnreadCount(state.unreadCount)
        setLoading(state is WallState.Loading)
        actionBar?.menu?.getItem(MENU_ITEM_UPDATE)?.isVisible = listState !is WallState.Loading
    }

    override fun isSkeletonVisible(): Boolean {
        return false
    }

    override fun isShowBottomOverlayChat(): Boolean {
        return false
    }

    override fun isShowBottomView(): Boolean {
        return false
    }

    override fun markWallMessagesRead(maxUnreadDate: Int) {
        viewModel.markRead(maxUnreadDate)
    }

    override fun onPageDownClicked() {
        viewModel.loadNextPage()
        super.onPageDownClicked()
    }

    private fun createAdapter() = object : ChatActivity.ChatActivityAdapter(parentActivity) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val vh = super.onCreateViewHolder(parent, viewType)
            (vh.itemView as? ChatMessageCell)?.apply {
                delegate = object : ChatActivity.ChatMessageCellDelegate() {
                    override fun didPressCommentButton(cell: ChatMessageCell) {
                        val group = cell.currentMessagesGroup
                        val message = group?.messages?.getOrNull(0) ?: cell.messageObject
                        val maxReadId = message.messageOwner.replies?.read_max_id ?: -1
                        val linkedChatId = message.messageOwner.replies?.channel_id ?: 0
                        openDiscussionMessageChat(cell.currentChat.id, message, message.id, linkedChatId, maxReadId, 0, null)
                    }

                    override fun didPressDahlChannelButton(cell: ChatMessageCell) {
                        val args = Bundle()
                        args.putLong("chat_id", -cell.messageObject.dialogId)
                        args.putInt("message_id", max(1, cell.messageObject.id))
                        val chatActivity = ChatActivity(args)
                        presentFragment(chatActivity)
                    }
                }
            }
            return vh
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            (holder.itemView as? ChatLoadingCell)?.setProgressVisible(true)
            if (position <= 10) {
                viewModel.loadNextPage()
            }
        }
    }
}