package ru.tusco.messenger.ui.wall

import org.telegram.messenger.MessageObject

sealed class WallState(open val unreadCount: Int = 0){
    data object Initial: WallState()
    data class Loading(override val unreadCount: Int): WallState(unreadCount)
    data class LoadingNextPage(override val unreadCount: Int): WallState(unreadCount)
    data object Empty: WallState()
    data class NewData(val data: List<MessageObject> = emptyList(), override val unreadCount: Int): WallState(unreadCount)
}