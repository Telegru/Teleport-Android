package ru.tusco.messenger.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.DialogObject
import org.telegram.messenger.ImageReceiver
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.RecyclerListView
import org.telegram.ui.Components.VectorAvatarThumbDrawable
import org.telegram.ui.Stories.StoriesUtilities.AvatarStoryParams
import ru.tusco.messenger.settings.DahlSettings

class RecentChatsPanel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), RecentChatViewHolder.Delegate {

    private val emptyView: AppCompatTextView
    private val listView: RecyclerListView
    private val adapter: RecentChatAdapter

    init {
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray))

        emptyView = AppCompatTextView(context)
        emptyView.text = LocaleController.getString(R.string.EmptyRecentChats)
        emptyView.setTextColor(Theme.getColor(Theme.key_dialogTextGray))
        emptyView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val margin = AndroidUtilities.dp(16f)
        emptyView.setPadding(margin, 0, margin, 0)
        addView(emptyView, LayoutHelper.createFrame(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER))
//        emptyView.visibility = View.GONE

        adapter = RecentChatAdapter(UserConfig.selectedAccount, this)
        listView = RecyclerListView(context)
        listView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        listView.adapter = adapter

        addView(listView, LayoutHelper.createFrame(ViewGroup.LayoutParams.MATCH_PARENT, 48f, Gravity.START, 4f, 4f, 4f, 4f))

    }

    override fun openChat(dialogId: Long) {

    }

    override fun showContextMenu(dialogId: Long) {

    }

    fun updateList(){
        val chats = DahlSettings.getRecentChats(UserConfig.selectedAccount)
        adapter.update(chats)
        emptyView.visibility = if(chats.isEmpty()) VISIBLE else GONE
    }

}

internal class RecentChatAdapter(private val currentAccount: Int, private val delegate: RecentChatViewHolder.Delegate) : RecyclerListView.SelectionAdapter() {

    private val items = mutableListOf<TLRPC.Dialog>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val container = FrameLayout(parent.context)
        return RecentChatViewHolder(container, currentAccount, delegate)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dialog = items[position]
        (holder as RecentChatViewHolder).bind(dialog)
    }

    override fun getItemCount(): Int = items.size

    override fun isEnabled(holder: RecyclerView.ViewHolder?): Boolean = true

    fun update(items: List<TLRPC.Dialog>){
        val callback = DialogsCallback(this.items, items)
        val result = DiffUtil.calculateDiff(callback)
        this.items.clear()
        this.items.addAll(items)
        result.dispatchUpdatesTo(this)
    }

    private class DialogsCallback(val oldItems: List<TLRPC.Dialog>, val newItems: List<TLRPC.Dialog>): DiffUtil.Callback(){
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition].id == newItems[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition] == newItems[newItemPosition]
    }

}

internal class RecentChatViewHolder(private val container: FrameLayout, private val currentAccount: Int, private val delegate: Delegate) :
    RecyclerListView.Holder(container) {

    interface Delegate {

        fun openChat(dialogId: Long)
        fun showContextMenu(dialogId: Long)
    }

    private var dialog: TLRPC.Dialog? = null
    private val avatarImage: ImageReceiver
    private val avatarDrawable = AvatarDrawable()

    private val avatarParams: AvatarStoryParams = object : AvatarStoryParams(false) {
        override fun openStory(dialogId: Long, onDone: Runnable) {
            delegate.openChat(dialogId)
        }

        override fun onLongPress() {
            dialog?.let { delegate.showContextMenu(it.id) }
        }
    }

    init {
        val frame = FrameLayout(itemView.context)
        container.addView(frame, LayoutHelper.createFrame(56, 48f))

        avatarImage = ImageReceiver(frame)
        avatarImage.setRoundRadius(AndroidUtilities.dp(if (DahlSettings.rectangularAvatars) 3f else 16f))
        avatarImage.setAllowLoadingOnAttachedOnly(true)
        val avatarSize = AndroidUtilities.dp(32f)
        val avatarLeft = AndroidUtilities.dp(12f)
        val avatarTop = AndroidUtilities.dp(8f)
        avatarParams.originalAvatarRect.set(
            avatarLeft.toFloat(),
            avatarTop.toFloat(),
            (avatarLeft + avatarSize).toFloat(),
            (avatarTop + avatarSize).toFloat()
        )
    }

    fun bind(dialog: TLRPC.Dialog) {
        this.dialog = dialog
        val messagesController = MessagesController.getInstance(currentAccount)
        var user: TLRPC.User? = null
        var chat: TLRPC.Chat? = null
        if (dialog.id != 0L) {
            if (DialogObject.isEncryptedDialog(dialog.id )) {
                messagesController.getEncryptedChat(DialogObject.getEncryptedChatId(dialog.id ))?.let { encryptedChat ->
                    user = messagesController.getUser(encryptedChat.user_id)
                }
            } else if(DialogObject.isUserDialog(dialog.id )){
                user = messagesController.getUser(dialog.id )
            } else{
                chat = messagesController.getChat(-dialog.id )
            }
        }
        if(user != null){
            avatarDrawable.setInfo(currentAccount, user)
            avatarImage.setForUserOrChat(user, avatarDrawable, null, true, VectorAvatarThumbDrawable.TYPE_SMALL, false)
        }else if(chat != null){
            avatarDrawable.setInfo(currentAccount, chat)
            avatarImage.setForUserOrChat(chat, avatarDrawable)
        }
    }
}
