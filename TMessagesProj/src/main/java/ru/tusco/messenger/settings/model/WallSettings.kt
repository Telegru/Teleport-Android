package ru.tusco.messenger.settings.model

import android.content.SharedPreferences

data class WallSettings(
    val archivedChannels: Boolean = true,
    val showInChats: Boolean = true,
    val excludedChannels: List<Long> = emptyList(),
) {

    companion object{
        private const val KEY_ARCHIVED_CHANNELS = "wall_show_archived_channels"
        private const val KEY_SHOW_IN_CHATS = "wall_show_in_chats"
        private const val KEY_EXCLUDED_CHANNELS = "wall_excluded_channels"

        val keys by lazy {  setOf(KEY_ARCHIVED_CHANNELS, KEY_SHOW_IN_CHATS, KEY_EXCLUDED_CHANNELS) }
    }

    constructor(sharedPreferences: SharedPreferences): this(
        archivedChannels = sharedPreferences.getBoolean(KEY_ARCHIVED_CHANNELS, true),
        showInChats = sharedPreferences.getBoolean(KEY_SHOW_IN_CHATS, true),
        excludedChannels = sharedPreferences.getString(KEY_EXCLUDED_CHANNELS, "")?.split(";")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
    )

    fun save(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()
            .putBoolean(KEY_ARCHIVED_CHANNELS, archivedChannels)
            .putBoolean(KEY_SHOW_IN_CHATS, showInChats)
            .putString(KEY_EXCLUDED_CHANNELS, excludedChannels.joinToString(";"))
            .apply()
    }


}