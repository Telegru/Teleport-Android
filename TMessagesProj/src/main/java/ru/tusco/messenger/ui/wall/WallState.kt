package ru.tusco.messenger.ui.wall

import org.telegram.messenger.MessageObject

data class WallState(
    val loading: Boolean = false,
    val data: List<MessageObject> = emptyList(),

)