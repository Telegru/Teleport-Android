package ru.tusco.messenger.settings

import android.view.View
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment

class ChatsSettingsActivity : UniversalFragment() {

    companion object {
        const val SWITCH_FOLDERS_AT_BOTTOM = 1
        const val SWITCH_HIDE_ALL_CHATS_FOLDER = 2
        const val SWITCH_FOLDERS_INFINITE_SCROLL = 3
        const val SWITCH_SHOW_FOLDERS_TABS = 4

        const val SWITCH_CONFIRM_CALL = 5
        const val SWITCH_CONFIRM_AUDIO_MESSAGE = 6
        const val SELECTOR_VIDEO_CAMERA = 7

        const val SWITCH_RECENT_CHATS = 8
    }

    override fun getTitle(): CharSequence {
        return getString(R.string.ChatsSettings)
    }

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
//        items?.add(UItem.asHeader(getString(R.string.NeedConfirm)))
//        items?.add(UItem.asCheck(SWITCH_CONFIRM_CALL, getString(R.string.ConfirmCalling)).setChecked(DahlSettings.confirmCall))
//        items?.add(UItem.asCheck(SWITCH_CONFIRM_AUDIO_MESSAGE, getString(R.string.ConfirmAudioMessage)).setChecked(DahlSettings.confirmAudioMessage))
//        items?.add(
//            UItem.asButton(
//                SELECTOR_VIDEO_CAMERA,
//                getString(R.string.ConfirmVideoMessageCamera),
//                getString(DahlSettings.videoMessageCamera.title)
//            )
//        )
//
//        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.RecentChats)))
        items?.add(UItem.asCheck(SWITCH_RECENT_CHATS, getString(R.string.EnablePanel)).setChecked(DahlSettings.recentChats))
        items?.add(UItem.asShadow(getString(R.string.RecentChatsInfo)))

        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.ChatsFolders)))
        items?.add(UItem.asCheck(SWITCH_FOLDERS_AT_BOTTOM, getString(R.string.FoldersAtBottom)).setChecked(DahlSettings.isFoldersTabsAtBottom))
        items?.add(UItem.asCheck(SWITCH_FOLDERS_INFINITE_SCROLL, getString(R.string.FoldersInfiniteScrolling)).setChecked(DahlSettings.isFoldersTabInfiniteScroll))
        items?.add(UItem.asCheck(SWITCH_SHOW_FOLDERS_TABS, getString(R.string.ShowAllFolders)).setChecked(!DahlSettings.isHiddenFoldersTabs))
        items?.add(UItem.asCheck(SWITCH_HIDE_ALL_CHATS_FOLDER, getString(R.string.HideAllChatsFolder)).setChecked(DahlSettings.isHiddenAllChatsFolder))
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
        when (item?.id) {
            SWITCH_CONFIRM_CALL -> DahlSettings.confirmCall = !DahlSettings.confirmCall
            SWITCH_CONFIRM_AUDIO_MESSAGE -> DahlSettings.confirmAudioMessage = !DahlSettings.confirmAudioMessage
            SELECTOR_VIDEO_CAMERA -> {}

            SWITCH_RECENT_CHATS -> DahlSettings.recentChats = !DahlSettings.recentChats

            SWITCH_FOLDERS_AT_BOTTOM -> DahlSettings.isFoldersTabsAtBottom = !DahlSettings.isFoldersTabsAtBottom
            SWITCH_HIDE_ALL_CHATS_FOLDER -> DahlSettings.isHiddenAllChatsFolder = !DahlSettings.isHiddenAllChatsFolder
            SWITCH_FOLDERS_INFINITE_SCROLL -> DahlSettings.isFoldersTabInfiniteScroll = !DahlSettings.isFoldersTabInfiniteScroll
            SWITCH_SHOW_FOLDERS_TABS -> DahlSettings.isHiddenFoldersTabs = !DahlSettings.isHiddenFoldersTabs

            else -> {}
        }
        listView.adapter.update(true)
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean {
        return false
    }


}