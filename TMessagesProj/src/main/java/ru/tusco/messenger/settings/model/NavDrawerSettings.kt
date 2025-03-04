package ru.tusco.messenger.settings.model

import android.content.Context
import android.content.SharedPreferences
import org.telegram.messenger.R
import org.telegram.ui.Adapters.DrawerLayoutAdapter

data class NavDrawerSettings(
    val proxy: Boolean = false,
    val profile: Boolean = true,
    val changeStatus: Boolean = false,
    val wallet: Boolean = false,
    val newGroup: Boolean = false,
    val contacts: Boolean = true,
    val calls: Boolean = true,
    val savedMessages: Boolean = false,
    val inviteFriends: Boolean = false,
    val telegramFeatures: Boolean = false,
) {

    constructor(sharedPreferences: SharedPreferences) :
            this(
                proxy = sharedPreferences.getBoolean("nav_drawer_proxy", false),
                profile = sharedPreferences.getBoolean("nav_drawer_profile", true),
                changeStatus = sharedPreferences.getBoolean("nav_drawer_change_status", false),
                wallet = sharedPreferences.getBoolean("nav_drawer_wallet", false),
                newGroup = sharedPreferences.getBoolean("nav_drawer_new_group", false),
                contacts = sharedPreferences.getBoolean("nav_drawer_contacts", true),
                calls = sharedPreferences.getBoolean("nav_drawer_calls", true),
                savedMessages = sharedPreferences.getBoolean("nav_drawer_saved_messages", false),
                inviteFriends = sharedPreferences.getBoolean("nav_drawer_invite_friends", false),
                telegramFeatures = sharedPreferences.getBoolean("nav_drawer_telegram_features", false),
            )

    fun save(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()
            .putBoolean("nav_drawer_proxy", proxy)
            .putBoolean("nav_drawer_profile", profile)
            .putBoolean("nav_drawer_change_status", changeStatus)
            .putBoolean("nav_drawer_wallet", wallet)
            .putBoolean("nav_drawer_new_group", newGroup)
            .putBoolean("nav_drawer_contacts", contacts)
            .putBoolean("nav_drawer_calls", calls)
            .putBoolean("nav_drawer_saved_messages", savedMessages)
            .putBoolean("nav_drawer_invite_friends", inviteFriends)
            .putBoolean("nav_drawer_telegram_features", telegramFeatures)
            .apply()
    }

    fun isEnabled(itemId: Int): Boolean =
        when (itemId) {
            DrawerLayoutAdapter.ID_PROXY -> proxy
            DrawerLayoutAdapter.ID_PROFILE -> profile
            DrawerLayoutAdapter.ID_CHANGE_STATUS -> changeStatus
            DrawerLayoutAdapter.ID_NEW_GROUP -> newGroup
            DrawerLayoutAdapter.ID_CONTACTS -> contacts
            DrawerLayoutAdapter.ID_CALLS -> calls
            DrawerLayoutAdapter.ID_SAVED_MESSAGES -> savedMessages
            DrawerLayoutAdapter.ID_INVITE_FRIENDS -> inviteFriends
            DrawerLayoutAdapter.ID_TELEGRAM_FEATURES -> telegramFeatures
            else -> true
        }

    fun getInfoText(context: Context, isPremium: Boolean): String {
        var count = 0
        if (proxy) count++
        if (profile) count++
        if (isPremium && changeStatus) count++
        if (wallet) count++
        if (newGroup) count++
        if (contacts) count++
        if (calls) count++
        if (savedMessages) count++
        if (inviteFriends) count++
        if (telegramFeatures) count++

        return context.resources.getQuantityString(R.plurals.NavDrawerItems, count, count)

    }

}