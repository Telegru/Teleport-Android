package ru.tusco.messenger.settings

import android.view.View
import android.widget.Toast
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment
import ru.tusco.messenger.settings.DahlSettings.ICON_REPLACEMENT_VKUI
import ru.tusco.messenger.settings.DahlSettings.NO_REPLACEMENT

class DahlSettingsActivity: UniversalFragment() {
    companion object {
        const val SWITCH_ICONS = 1
        const val CHATS = 2
    }

    override fun getTitle(): CharSequence {
        return getString(R.string.SettingsDahl)
    }

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
        items?.add(UItem.asSwitch(SWITCH_ICONS, getString(R.string.IconsVkUI)).setChecked(
            DahlSettings.iconReplacement == ICON_REPLACEMENT_VKUI)
        )
        items?.add(UItem.asButton(CHATS, R.drawable.msg2_discussion, getString(R.string.ChatsSettings)))
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
        var showRestartAppMessage = false
        when (item?.id) {
            SWITCH_ICONS -> {
                if (item.checked) {
                    DahlSettings.iconReplacement = NO_REPLACEMENT
                } else {
                    DahlSettings.iconReplacement = ICON_REPLACEMENT_VKUI
                }
                showRestartAppMessage = true
            }
            CHATS -> {
                presentFragment(ChatsSettingsActivity())
            }
            else -> {}
        }
        if(showRestartAppMessage) {
            Toast.makeText(context, getString(R.string.RestartToast), Toast.LENGTH_SHORT).show()
        }
        listView.adapter.update(true)
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean {
        return false
    }
}
