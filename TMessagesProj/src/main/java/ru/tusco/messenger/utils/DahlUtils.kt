package ru.tusco.messenger.utils

import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.DialogObject
import org.telegram.messenger.MessagesController
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC
import ru.tusco.messenger.settings.DahlSettings

object DahlUtils {

    @JvmStatic
    val chatCellHeight: Int
        get() {
            val dps = when (DahlSettings.chatListLines) {
                1 -> 52f
                3 -> 78f
                else -> 72f
            }
            return AndroidUtilities.dp(dps)
        }

    @JvmStatic
    val wallChannels: List<TLRPC.Dialog>
        get() {
            val wallSettings = DahlSettings.wallSettings
            return MessagesController.getInstance(UserConfig.selectedAccount).dialogsChannelsOnly
                .filter { ch -> (ch.folder_id != 1 || wallSettings.archivedChannels) && !wallSettings.excludedChannels.contains(ch.id) }
        }

    @JvmStatic
    val unreadWallChannels: List<TLRPC.Dialog>
        get() {
            val wallSettings = DahlSettings.wallSettings
            return MessagesController.getInstance(UserConfig.selectedAccount).dialogsChannelsOnly
                .filter { ch ->
                    ch.unread_count > 0 && (ch.folder_id != 1 || wallSettings.archivedChannels) && !wallSettings.excludedChannels.contains(ch.id)
                }
        }
}