package ru.tusco.messenger.settings

import android.view.View
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment

class ChatsSettingsActivity : UniversalFragment() {

    companion object {
        const val SWITCH_FOLDERS_AT_BOTTOM = 1
        const val SWITCH_HIDE_ALL_CHATS_FOLDER = 2
        const val SWITCH_FOLDERS_INFINITE_SCROLL = 3
        const val SWITCH_HIDE_FOLDERS_TABS = 4
    }

    override fun getTitle(): CharSequence {
        return LocaleController.getString(R.string.ChatsSettings)
    }

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
        items?.add(UItem.asHeader(LocaleController.getString(R.string.ChatsFolders)))

        items?.add(
            UItem.asCheck(SWITCH_FOLDERS_AT_BOTTOM, LocaleController.getString(R.string.FoldersAtBottom)).setChecked(
                DahlSettings.isFoldersTabsAtBottom
            )
        )
        items?.add(
            UItem.asCheck(SWITCH_FOLDERS_INFINITE_SCROLL, LocaleController.getString(R.string.FoldersInfiniteScrolling)).setChecked(
                DahlSettings.isFoldersTabInfiniteScroll
            )
        )
        items?.add(UItem.asShadow(-3, null))
        items?.add(UItem.asHeader(LocaleController.getString(R.string.HideFolders)))
        items?.add(
            UItem.asCheck(SWITCH_HIDE_FOLDERS_TABS, LocaleController.getString(R.string.AllChats)).setChecked(
                DahlSettings.isHiddenFoldersTabs
            )
        )
        items?.add(
            UItem.asCheck(SWITCH_HIDE_ALL_CHATS_FOLDER, LocaleController.getString(R.string.HideAllChatsFolder)).setChecked(
                DahlSettings.isHiddenAllChatsFolder
            )
        )
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
        when (item?.id) {
            SWITCH_FOLDERS_AT_BOTTOM -> {
                DahlSettings.isFoldersTabsAtBottom = !DahlSettings.isFoldersTabsAtBottom
            }
            SWITCH_HIDE_ALL_CHATS_FOLDER -> {
                DahlSettings.isHiddenAllChatsFolder = !DahlSettings.isHiddenAllChatsFolder
            }
            SWITCH_FOLDERS_INFINITE_SCROLL -> {
                DahlSettings.isFoldersTabInfiniteScroll = !DahlSettings.isFoldersTabInfiniteScroll
            }
            SWITCH_HIDE_FOLDERS_TABS -> {
                DahlSettings.isHiddenFoldersTabs = !DahlSettings.isHiddenFoldersTabs
            }
            else -> {}
        }
        listView.adapter.update(true)
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean {
        return false
    }


}