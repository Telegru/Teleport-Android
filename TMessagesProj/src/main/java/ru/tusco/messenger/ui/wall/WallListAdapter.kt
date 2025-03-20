package ru.tusco.messenger.ui.wall

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.telegram.messenger.MessageObject
import org.telegram.ui.Cells.BaseCell
import org.telegram.ui.Cells.ChatMessageCell
import org.telegram.ui.Components.RecyclerListView

internal class WallListAdapter(private val currentUser: Int, private val loadNextPage: (Int) -> Unit) : RecyclerListView.SelectionAdapter() {

    private val messages = mutableListOf<MessageObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = WallMessageCell(parent.context, currentUser)
        return RecyclerListView.Holder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        message.needDrawAvatar()
        val messageCell = (holder.itemView as ChatMessageCell)
        messageCell.setMessageObject(message, null, false, false)
        if(position > itemCount - 10){
            loadNextPage.invoke(itemCount)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemId(position: Int): Long = messages[position].stableId.toLong()

    override fun isEnabled(holder: RecyclerView.ViewHolder): Boolean = true

    fun insert(messages: List<MessageObject>){
        val pos = messages.size
        this.messages.addAll(messages)
        notifyItemRangeChanged(pos, messages.size)
    }
}

@SuppressLint("ViewConstructor")
internal class WallMessageCell(context: Context, currentUser: Int) : ChatMessageCell(context, currentUser) {

    init {
//        isChat = true
    }

    override fun needDrawAvatar(): Boolean {
        return true
    }


}