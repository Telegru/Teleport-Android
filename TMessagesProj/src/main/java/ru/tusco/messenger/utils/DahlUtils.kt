package ru.tusco.messenger.utils

import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.SharedConfig

object DahlUtils {

    @JvmStatic
    val chatCellHeight: Int
        get() {
            val dps = when (SharedConfig.chatListLines) {
                1 -> 52f
                3 -> 78f
                else -> 72f
            }
            return AndroidUtilities.dp(dps)
        }
}