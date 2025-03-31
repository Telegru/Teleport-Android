package ru.tusco.messenger.settings

import android.view.View
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment

class WallSettingsActivity: UniversalFragment() {

    companion object {
        const val ARCHIVE = 1
        const val CHAT_LIST = 2
    }

    override fun getTitle(): CharSequence = getString(R.string.WallSettings)


    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
        DahlSettings.wallSettings.apply {
            items?.add(UItem.asHeader(getString(R.string.NeedShow)))
            items?.add(UItem.asCheck(ARCHIVE, getString(R.string.ArchiveChannels), getString(R.string.ArchiveChannelsInfo)).setChecked(archivedChannels))
        }
    }

    override fun onClick(item: UItem, view: View?, position: Int, x: Float, y: Float) {

        when (item.id) {
            ARCHIVE -> DahlSettings.wallSettings = DahlSettings.wallSettings.let{ it.copy(archivedChannels = !it.archivedChannels) }
            CHAT_LIST ->  DahlSettings.wallSettings = DahlSettings.wallSettings.let{ it.copy(showInChats = !it.showInChats) }

        }
        listView.adapter.update(true)
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean = false
}