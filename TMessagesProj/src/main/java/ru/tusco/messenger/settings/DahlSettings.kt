package ru.tusco.messenger.settings

import android.app.Activity
import android.content.SharedPreferences
import androidx.annotation.StringRes
import org.telegram.messenger.AccountInstance
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.messenger.SharedConfig
import org.telegram.messenger.SharedConfig.ProxyInfo
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.ConnectionsManager
import org.telegram.ui.LaunchActivity
import ru.tusco.messenger.Extra
import ru.tusco.messenger.icons.BaseIconReplacement
import ru.tusco.messenger.icons.IconReplacementNone
import ru.tusco.messenger.icons.VKUiIconReplacement
import ru.tusco.messenger.settings.model.NavDrawerSettings

object DahlSettings {

    val isSquaringEnabled = true
    val avatarCornerRadius = if (isSquaringEnabled) 16 else AndroidUtilities.dp(28f)

    private val sharedPreferences: SharedPreferences =
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putInt(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putStringSet(key: String, value: Set<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, value)
        editor.apply()
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    const val NO_REPLACEMENT = 0
    const val ICON_REPLACEMENT_VKUI = 1

    enum class VideoMessageCamera {
        SELECT, FRONT, BACK;

        @get:StringRes
        val title: Int
            get() = when (this) {
                SELECT -> R.string.AlwaysAsk
                FRONT -> R.string.VoipFrontCamera
                BACK -> R.string.VoipBackCamera
            }
    }

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
        get() {
            val proxyInfo = ProxyInfo(Extra.PROXY_ADDRESS, Extra.PROXY_PORT, null, null, Extra.PROXY_SECRET)
            val current = SharedConfig.currentProxy
            return SharedConfig.isProxyEnabled() && current.link == proxyInfo.link
        }
        set(value) {
            val editor = MessagesController.getGlobalMainSettings().edit()
            if (value) {
                val proxyInfo = ProxyInfo(Extra.PROXY_ADDRESS, Extra.PROXY_PORT, null, null, Extra.PROXY_SECRET)
                SharedConfig.addProxy(proxyInfo)
                SharedConfig.currentProxy = proxyInfo
                editor
                    .putBoolean("proxy_enabled", true)
                    .putString("proxy_ip", proxyInfo.address)
                    .putString("proxy_pass", proxyInfo.password)
                    .putString("proxy_user", proxyInfo.username)
                    .putInt("proxy_port", proxyInfo.port)
                    .putString("proxy_secret", proxyInfo.secret)
                    .apply()
                ConnectionsManager.setProxySettings(
                    true,
                    proxyInfo.address,
                    proxyInfo.port,
                    proxyInfo.username,
                    proxyInfo.password,
                    proxyInfo.secret
                )
            } else {
                editor
                    .putBoolean("proxy_enabled", false)
                    .apply()

                ConnectionsManager.setProxySettings(
                    false,
                    "",
                    1080,
                    "",
                    "",
                    ""
                )
            }
        }

    @JvmStatic
    var isHidePhoneNumber: Boolean
        get() = sharedPreferences.getBoolean("hide_phone_number_in_menu", false)
        set(value) {
            putBoolean("hide_phone_number_in_menu", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var hideMessageReadStatus: Boolean
        get() = sharedPreferences.getBoolean("hide_message_read_status", false)
        set(value) {
            putBoolean("hide_message_read_status", value)
//            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isOffline: Boolean
        get() = sharedPreferences.getBoolean("offline_mode", false)
        set(value) {
            putBoolean("offline_mode", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isShowBottomPanelInChannels: Boolean
        get() = sharedPreferences.getBoolean("show_bottom_panel_in_channels", true)
        set(value) {
            putBoolean("show_bottom_panel_in_channels", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isEnabledPersonalColors: Boolean
        get() = sharedPreferences.getBoolean("enabled_personal_colors", true)
        set(value) {
            putBoolean("enabled_personal_colors", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var isHiddenHelpBlock: Boolean
        get() = sharedPreferences.getBoolean("hide_help_block", false)
        set(value) {
            putBoolean("hide_help_block", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var navigationDrawerItems: NavDrawerSettings
        get() = NavDrawerSettings(sharedPreferences)
        set(value) {
            value.save(sharedPreferences)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var hidePremium: Boolean
        get() = sharedPreferences.getBoolean("hide_premium", false)
        set(value) {
            putBoolean("hide_premium", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var statusesIcons: Boolean
        get() = sharedPreferences.getBoolean("statuses_icons", true)
        set(value) {
            putBoolean("statuses_icons", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var animatedAvatars: Boolean
        get() = sharedPreferences.getBoolean("animated_avatars", true)
        set(value) {
            putBoolean("animated_avatars", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var customChannelsWallpapers: Boolean
        get() = sharedPreferences.getBoolean("custom_channels_wallpapers", true)
        set(value) {
            putBoolean("custom_channels_wallpapers", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var animatedReactions: Boolean
        get() = sharedPreferences.getBoolean("animated_reactions", true)
        set(value) {
            putBoolean("animated_reactions", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var animatedPremiumStickers: Boolean
        get() = sharedPreferences.getBoolean("animated_premium_stickers", true)
        set(value) {
            putBoolean("animated_premium_stickers", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var touchOnPremiumStickers: Boolean
        get() = sharedPreferences.getBoolean("touch_on_premium_stickers", true)
        set(value) {
            putBoolean("touch_on_premium_stickers", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var hideStories: Boolean
        get() = sharedPreferences.getBoolean("hide_stories", false)
        set(value) {
            putBoolean("hide_stories", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var hideAddStory: Boolean
        get() = sharedPreferences.getBoolean("hide_add_story", false)
        set(value) {
            putBoolean("hide_add_story", value)
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    @JvmStatic
    var hideViewedStories: Boolean
        get() = sharedPreferences.getBoolean("hide_viewed_stories", false)
        set(value) {
            putBoolean("hide_viewed_stories", value)
            AccountInstance.getInstance(UserConfig.selectedAccount).messagesController.storiesController.scheduleSort()
            LaunchActivity.getSafeLastFragment().parentLayout.rebuildFragments(0)
        }

    var confirmCall: Boolean
        get() = sharedPreferences.getBoolean("confirm_call", false)
        set(value) {
            putBoolean("confirm_call", value)
        }

    var confirmAudioMessage: Boolean
        get() = sharedPreferences.getBoolean("confirm_audio_message", false)
        set(value) {
            putBoolean("confirm_audio_message", value)
        }

    var videoMessageCamera: VideoMessageCamera
        get() {
            val ordinal = sharedPreferences.getInt("video_message_camera", 0)
            return VideoMessageCamera.entries.getOrNull(ordinal) ?: VideoMessageCamera.SELECT
        }
        set(value) = putInt("video_message_camera", value.ordinal)

    var recentChats: Boolean
        get() = sharedPreferences.getBoolean("recent_chats", false)
        set(value) {
            putBoolean("recent_chats", value)
        }
}