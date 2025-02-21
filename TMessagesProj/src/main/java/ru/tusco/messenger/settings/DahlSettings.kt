package ru.tusco.messenger.settings

import android.app.Activity
import android.content.SharedPreferences
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.ui.LaunchActivity
import ru.tusco.messenger.icons.BaseIconReplacement
import ru.tusco.messenger.icons.IconReplacementNone
import ru.tusco.messenger.icons.VKUiIconReplacement

object DahlSettings {

    val isSquaringEnabled = true
    val avatarCornerRadius = if (isSquaringEnabled) 16 else AndroidUtilities.dp(28f)

    private val sharedPreferences: SharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean) {
        val preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putInt(key: String, value: Int) {
        val preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    const val NO_REPLACEMENT = 0
    const val ICON_REPLACEMENT_VKUI = 1

    var iconReplacement
        get() = sharedPreferences.getInt("AP_Icon_Replacements", ICON_REPLACEMENT_VKUI)
        set(value) {
            putInt("AP_Icon_Replacements", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    fun getCurrentIconPack(): BaseIconReplacement {
        return when (iconReplacement) {
            NO_REPLACEMENT -> IconReplacementNone()
            ICON_REPLACEMENT_VKUI -> VKUiIconReplacement()
            else -> IconReplacementNone()
        }
    }

    @JvmStatic
    var isFoldersTabsAtBottom: Boolean
        get() = sharedPreferences.getBoolean("folders_tabs_at_bottom", true)
        set(value) {
            putBoolean("folders_tabs_at_bottom", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isHiddenFoldersTabs: Boolean
        get() = sharedPreferences.getBoolean("hidden_folders_tabs", false)
        set(value) {
            putBoolean("hidden_folders_tabs", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isHiddenAllChatsFolder: Boolean
        get() = sharedPreferences.getBoolean("hidden_all_chats_folder", false)
        set(value) {
            putBoolean("hidden_all_chats_folder", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isFoldersTabInfiniteScroll: Boolean
        get() = sharedPreferences.getBoolean("folders_tabs_infinite_scroll", false)
        set(value) {
            putBoolean("folders_tabs_infinite_scroll", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isProxyEnabled: Boolean
        get() = sharedPreferences.getBoolean("dahl_proxy_enabled", false)
        set(value) {
            putBoolean("dahl_proxy_enabled", value)
//            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isHidePhoneNumber: Boolean
        get() = sharedPreferences.getBoolean("hide_phone_number_in_menu", false)
        set(value) {
            putBoolean("hide_phone_number_in_menu", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isMessagesReadStatus: Boolean
        get() = sharedPreferences.getBoolean("messages_read_status", false)
        set(value) {
            putBoolean("messages_read_status", value)
//            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isOffline: Boolean
        get() = sharedPreferences.getBoolean("offline_mode", false)
        set(value) {
            putBoolean("offline_mode", value)
//            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }
}