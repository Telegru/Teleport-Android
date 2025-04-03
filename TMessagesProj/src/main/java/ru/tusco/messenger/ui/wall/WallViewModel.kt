package ru.tusco.messenger.ui.wall

import android.content.SharedPreferences
import androidx.annotation.UiThread
import androidx.collection.LongSparseArray
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.DispatchQueue
import org.telegram.messenger.FileLog
import org.telegram.messenger.MessageObject
import org.telegram.messenger.MessagesController
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC.Dialog
import org.telegram.ui.ChatActivity
import ru.tusco.messenger.settings.DahlSettings
import ru.tusco.messenger.settings.model.WallSettings
import ru.tusco.messenger.ui.mvvm.BaseViewModel
import ru.tusco.messenger.utils.DahlUtils
import java.util.TreeSet
import kotlin.math.max
import kotlin.math.min

@UiThread
class WallViewModel : BaseViewModel<WallState>(WallState.Initial), NotificationCenter.NotificationCenterDelegate, SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val MESSAGES_SIZE = 50
    }

    private val queue = DispatchQueue("WallViewModel", true, Thread.MAX_PRIORITY)

    private val messagesController = MessagesController.getInstance(UserConfig.selectedAccount)
    private val notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount)

    private val classGuid = ConnectionsManager.generateClassGuid()
    private var loadIndex = 0

    private val messages =  LongSparseArray<List<MessageObject>>()
    private var channelsCount = 0
    private var channelsMessages = LongSparseArray<MutableList<MessageObject>>()
    private var cacheEndReached = LongSparseArray<Boolean>()
    private var messagesIds = LongSparseArray<TreeSet<Int>>()

    private val filteredChannels: List<Dialog>
        get() = DahlUtils.unreadWallChannels

    init {
        notificationCenter.addObserver(this, NotificationCenter.messagesDidLoad)
        DahlSettings.addListener(this)
    }

    override fun onDestroy() {
        DahlSettings.removeListener(this)
        notificationCenter.removeObserver(this, NotificationCenter.messagesDidLoad)
        queue.cleanupQueue()
        queue.recycle()
        super.onDestroy()
    }

    fun reload() {
        if(state.value is WallState.Loading) return
        FileLog.d("WallViewModel, reload")
        queue.cleanupQueue()
        channelsCount = 0
        messages.clear()
        channelsMessages.clear()
        cacheEndReached.clear()

        messagesIds.clear()
        loadIndex++
        var unreadCount = 0
        val channels = filteredChannels
        channelsCount = channels.size
        for (ch in channels) {
            unreadCount += ch.unread_count
            messagesController.loadMessages(
                ch.id,
                0,
                false,
                min(MESSAGES_SIZE, ch.unread_count),
                0,
                0,
                true,
                0,
                classGuid,
                MessagesController.LOAD_FROM_UNREAD,
                0,
                ChatActivity.MODE_DEFAULT,
                0,
                0,
                loadIndex,
                false
            )
        }
        if(channels.isNotEmpty()){
            state.setValue(WallState.Loading(unreadCount = unreadCount))
        }else{
            state.setValue(WallState.Empty)
        }

    }

    fun loadNextPage() {
        val listState = state.value
        val canLoadMore = listState is WallState.NewData
        if (!canLoadMore) {
            return
        }
        messages.clear()
        loadIndex++
        val channels = filteredChannels
        channelsCount = 0
        for (ch in channels) {
            if(true == messagesIds.get(ch.id)?.contains(ch.top_message)){
                continue
            }
            channelsCount++
            val messages = channelsMessages.get(ch.id)
            val maxId = messages?.lastOrNull()?.id ?: ch.read_inbox_max_id
            val maxDate = messages?.lastOrNull()?.messageOwner?.date ?: 0
            val fromCache = !(cacheEndReached.get(ch.id) ?: false)

            messagesController.loadMessages(
                ch.id,
                0,
                false,
                min(MESSAGES_SIZE, ch.unread_count),
                maxId,
                0,
                fromCache,
                maxDate,
                classGuid,
                MessagesController.LOAD_FORWARD,
                0,
                ChatActivity.MODE_DEFAULT,
                0,
                0,
                loadIndex,
                false
            )
        }
        if(channelsCount > 0){
            state.setValue(WallState.LoadingNextPage(listState.unreadCount))
        }else{
            state.setValue(WallState.NewData(unreadCount = listState.unreadCount))
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences, key: String?) {
        FileLog.d("WallViewModel, onSharedPreferenceChanged, key: $key")
        if(key.isNullOrBlank()) return
        if(WallSettings.keys.contains(key)){
            reload()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun didReceivedNotification(id: Int, account: Int, vararg args: Any) {
        if (id == NotificationCenter.messagesDidLoad) {
            val guid = args[10] as Int
            if (guid != classGuid) {
                return
            }
            val channelId = args[0] as Long
            val count = args[1] as Int

            val objects = args[2] as List<MessageObject>
            val isCache = args[3] as Boolean
            val loadType = args[8] as Int
            var queryLoadIndex = args[11] as Int
            if (queryLoadIndex < 0) {
                queryLoadIndex = -queryLoadIndex
            }

            FileLog.d("WallViewModel, loadIndex: $loadIndex, queryLoadIndex: $queryLoadIndex, index: $loadIndex, isCache: $isCache, channelId: $channelId, count: $count, objects: ${objects.size}")
            if (queryLoadIndex != loadIndex) {
                return
            }
            if(loadType != MessagesController.LOAD_FROM_UNREAD && isCache && objects.size < count){
                cacheEndReached.put(channelId, true)
            }

            messages.append(channelId, objects)
            if (channelsCount == messages.size()) {
                processMessages(messages)
            }
        }
    }

    private fun processMessages(messages: LongSparseArray<List<MessageObject>>) {
        val channels = ArrayList(messagesController.dialogsChannelsOnly)
        queue.postRunnable {
            val result = mutableListOf<MessageObject>()
            for (ch in channels) {
                val list = messages[ch.id] ?: continue
                for(message in list){
                    if(message.id <= 0){
                        continue
                    }

                    if(true == messagesIds[ch.id]?.contains(message.id)){
                        continue
                    }
                     if(message.id > ch.read_inbox_max_id){
                        result.add(message)
                    }
                }
            }
            result.sortWith(object : Comparator<MessageObject> {
                override fun compare(o1: MessageObject, o2: MessageObject): Int {
                    var d = o1.messageOwner.date.compareTo(o2.messageOwner.date)
                    if(d != 0){
                        return d
                    }
                    d = o1.groupId.compareTo(o2.groupId)
                    if(d != 0){
                        return d
                    }
                    return o1.id.compareTo(o2.id)
                }
            })
            FileLog.d("processMessages, result size: ${result.size}")
            val msgs = mutableListOf<MessageObject>()
            var oldGroupId = 0L
            for(msg in result){
                if(msgs.size >= MESSAGES_SIZE - 10 && (!msg.hasValidGroupIdFast() || msg.groupId != oldGroupId)){
                   break
                }
                msgs.add(msg)
                oldGroupId = msg.groupId
            }

            AndroidUtilities.runOnUIThread {
                msgs.forEach {
                    it.isDahlWallMessage = true
                    if(!it.hasValidGroupIdFast()){
                        it.forceAvatar = true
                        it.resetLayout()
                    }
                    val list = channelsMessages.get(it.dialogId) ?: mutableListOf()
                    list.add(it)
                    channelsMessages.put(it.dialogId, list)
                    val ids = messagesIds[it.dialogId] ?: TreeSet()
                    ids.add(it.id)
                    messagesIds.put(it.dialogId, ids)
                }
                val unreadCount = state.value.unreadCount

                state.setValue(WallState.NewData(data = msgs, unreadCount = unreadCount))
            }
        }
    }

    fun markRead(date: Int) {
        FileLog.d("WallViewModel, markRead, date: $date")
        if(date <= 0) return

        var totalCount = 0
        for (i in 0 until channelsMessages.size()) {
            val dialogId = channelsMessages.keyAt(i)
            val messages = channelsMessages.valueAt(i)
            var message: MessageObject? = null
            var count = 0
            for (msg in messages){
                if(msg.messageOwner.date > date){
                    break
                }
                if(msg.isUnread){
                    msg.setIsRead()
                    count++
                }
                message = msg
            }
            message?.let{ m ->
//                messagesController.markDialogAsRead(dialogId, m.id, 0, m.messageOwner.date, false, 0, count, true, 0)
            }

            totalCount += count
        }

        val newUnreadCount = max(0, state.value.unreadCount - totalCount)

        when(state.value){
            is WallState.Loading -> state.setValue(WallState.Loading(unreadCount = newUnreadCount))
            is WallState.NewData -> state.setValue(WallState.NewData(unreadCount = newUnreadCount))
            is WallState.LoadingNextPage -> state.setValue(WallState.LoadingNextPage(unreadCount = newUnreadCount))
            else -> {}
        }
    }
}