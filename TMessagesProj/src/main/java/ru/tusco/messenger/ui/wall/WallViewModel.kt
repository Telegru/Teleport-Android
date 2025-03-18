package ru.tusco.messenger.ui.wall

import android.util.Log
import androidx.annotation.UiThread
import androidx.collection.LongSparseArray
import androidx.core.util.putAll
import org.telegram.messenger.AccountInstance
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.DispatchQueue
import org.telegram.messenger.MessageObject
import org.telegram.messenger.MessagesController
import org.telegram.messenger.MessagesStorage
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC
import ru.tusco.messenger.ui.mvvm.BaseViewModel

@UiThread
class WallViewModel : BaseViewModel<WallState>(WallState()), NotificationCenter.NotificationCenterDelegate {

    private val queue = DispatchQueue("WallViewModel", true, Thread.MAX_PRIORITY)

    init {
        loadData()
    }

    fun onResume() {
        val notificationCenter = AccountInstance.getInstance(UserConfig.selectedAccount).notificationCenter
        notificationCenter.addObserver(this, NotificationCenter.dialogsNeedReload)
//        loadData()
    }

    fun onPause() {
        val notificationCenter = AccountInstance.getInstance(UserConfig.selectedAccount).notificationCenter
        notificationCenter.removeObserver(this, NotificationCenter.dialogsNeedReload)
    }

    override fun onDestroy() {
        super.onDestroy()
        queue.cleanupQueue()
        queue.recycle()
    }

    override fun didReceivedNotification(id: Int, account: Int, vararg args: Any?) {
        when (id) {
//            NotificationCenter.dialogsNeedReload -> loadData()
        }
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
        Log.d("WallViewModel", "channels: " + channels.size + " messages: " + messages.size())
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
        result.sortBy { it.id }
        Log.d("WallViewModel", "result: " + result.size)
        AndroidUtilities.runOnUIThread {
            if (!isDestroyed) {
                Log.d("WallViewModel", "result: " + result.size)
                state.setValue(state.value.copy(loading = false, data = result))
            }
        }
        MessagesStorage.getInstance(UserConfig.selectedAccount)
            .loadChannelsUnreadMessages(0, 1000) {res ->
                Log.d("WallViewModel", "unreadMessages: " + res.size)
            }
    }
}