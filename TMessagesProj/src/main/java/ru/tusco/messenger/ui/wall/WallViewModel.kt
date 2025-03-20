package ru.tusco.messenger.ui.wall

import androidx.annotation.UiThread
import androidx.collection.LongSparseArray
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.DispatchQueue
import org.telegram.messenger.MessageObject
import org.telegram.messenger.MessagesController
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC
import ru.tusco.messenger.ui.mvvm.BaseViewModel

@UiThread
class WallViewModel : BaseViewModel<WallState>(WallState()){

    private val queue = DispatchQueue("WallViewModel", true, Thread.MAX_PRIORITY)

    init {
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        queue.cleanupQueue()
        queue.recycle()
    }

    private fun loadData() {
        val messagesController = MessagesController.getInstance(UserConfig.selectedAccount)
        val channels = ArrayList(messagesController.dialogsChannelsOnly)
        val messages = LongSparseArray<ArrayList<MessageObject>>()
        messages.putAll(messagesController.dialogMessage)
        state.setValue(state.value.copy(loading = true))
        queue.postRunnable { processMessages(channels, messages) }
    }

    private fun processMessages(channels: List<TLRPC.Dialog>, messages: LongSparseArray<ArrayList<MessageObject>>) {
        val result = mutableListOf<MessageObject>()
        for (channel in channels) {
            if(channel.unread_count == 0){
                continue
            }
            val messagesList = messages[channel.id] ?: continue
            for (message in messagesList) {
                if (message.isUnread) {
                    result.add(message)
                }
            }
        }
        result.sortBy { it.messageOwner.date }
        AndroidUtilities.runOnUIThread {
            if (!isDestroyed) {
                state.setValue(state.value.copy(data = result))
            }
        }
    }
}