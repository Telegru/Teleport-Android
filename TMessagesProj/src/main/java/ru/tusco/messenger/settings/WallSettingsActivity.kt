package ru.tusco.messenger.settings

import android.content.DialogInterface
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ContactsController
import org.telegram.messenger.LocaleController.formatString
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.tgnet.TLRPC.Chat
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.GroupCreateUserCell
import org.telegram.ui.Cells.TextCell
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment
import org.telegram.ui.UsersSelectActivity

class WallSettingsActivity : UniversalFragment() {

    companion object {
        const val ARCHIVE = 1
        const val CHAT_LIST = 2
        const val ADD_EXCLUSION = 3
        const val CLEAR_EXCLUSIONS = 4
        const val EXCLUSIONS_START = 5
    }

    private val excludedChats = mutableListOf<Chat>()

    override fun onResume() {
        super.onResume()
        listView?.adapter?.update(true)
    }

    override fun getTitle(): CharSequence = getString(R.string.WallSettings)


    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
        excludedChats.clear()
        DahlSettings.wallSettings.apply {
            items?.add(UItem.asHeader(getString(R.string.NeedShow)))
            items?.add(
                UItem.asCheck(ARCHIVE, getString(R.string.ArchiveChannels), getString(R.string.ArchiveChannelsInfo))
                    .setChecked(archivedChannels)
            )

            items?.add(UItem.asShadow(-3, null))

            items?.add(UItem.asHeader(getString(R.string.ExcludedChannels)))
            val addExclCell = TextCell(context).apply {
                textView.setTextSize(15)
                textView.setTypeface(AndroidUtilities.bold())
                val icon = ContextCompat.getDrawable(context, R.drawable.msg_contact_add)
                icon?.setBounds(0, 0, AndroidUtilities.dp(16f), AndroidUtilities.dp(16f))
                setTextAndIcon(getString(R.string.AddExclusion), icon, false)
                val color = Theme.key_windowBackgroundWhiteBlueHeader
                setColors(color, color)
                setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
            }
            items?.add(UItem.asCustom(ADD_EXCLUSION, addExclCell))

            var count = 0
            for (id in excludedChannels) {
                val ch = messagesController.dialogs_dict.get(id) ?: continue
                if (ch.folder_id == 1 && !archivedChannels) {
                    continue
                }
                val chat = messagesController.getChat(-ch.id) ?: continue
                val cell = GroupCreateUserCell(context, 1, 0, true)
                cell.setObject(chat, null, "")
                cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
                cell.tag = chat
                cell.setDrawDivider(true)
                items?.add(UItem.asCustom(EXCLUSIONS_START + count++, cell))
                excludedChats.add(chat)
            }
            items?.add(UItem.asShadow(getString(R.string.ExcludedChannelsInfo)))
            if(count > 0){
                val removeAllCell = TextCell(context).apply {
                    setText(getString(R.string.DeleteAllExclusions), false)
                    setTextColor(Theme.getColor(Theme.key_text_RedBold))
                    textView.setTypeface(AndroidUtilities.bold())
                    setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
                }
                items?.add(UItem.asCustom(CLEAR_EXCLUSIONS, removeAllCell))
            }
        }
    }

    override fun onClick(item: UItem, view: View?, position: Int, x: Float, y: Float) {
        when (item.id) {
            ARCHIVE -> {
                DahlSettings.wallSettings = DahlSettings.wallSettings.let { it.copy(archivedChannels = !it.archivedChannels) }
                listView.adapter.update(true)
            }

            CHAT_LIST -> DahlSettings.wallSettings = DahlSettings.wallSettings.let { it.copy(showInChats = !it.showInChats) }
            ADD_EXCLUSION -> presentFragment(UsersSelectActivity(UsersSelectActivity.TYPE_DAHL_WALL_EXCLUDED_CHANNELS))
            CLEAR_EXCLUSIONS -> showRemoveAllChannelAlert()
        }
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean {
        val id = item?.id ?: return false
        if(id < EXCLUSIONS_START) return false

        val chat = excludedChats[id - EXCLUSIONS_START]
        val excludedChannels = DahlSettings.wallSettings.excludedChannels.toMutableList()
        val canRemove = excludedChannels.removeAll { it == -chat.id  }
        if(canRemove){
            showRemoveChannelAlert(excludedChannels, chat)
        }
        return canRemove
    }

    private fun showRemoveChannelAlert(channels: List<Long>, chat: Chat){
        val nameColor = String.format("#%06X", (0xFFFFFF and Theme.getColor(Theme.key_dialogTextBlack)))

        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(formatString(R.string.DeleteExclusionAlert, nameColor, chat.title), Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(formatString(R.string.DeleteExclusionAlert, nameColor, chat.title))
        }

        val builder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.DeleteExclusion))
            .setMessage(message)
            .setNegativeButton(getString(R.string.Cancel), null)
            .setPositiveButton(getString(R.string.Delete)){_,_ ->
                DahlSettings.wallSettings = DahlSettings.wallSettings.copy(excludedChannels = channels)
                listView.adapter.update(true)
            }

        val dialog = builder.create()
        showDialog(dialog)
        (dialog.getButton(DialogInterface.BUTTON_POSITIVE) as? TextView)?.setTextColor(Theme.getColor(Theme.key_text_RedBold))
    }

    private fun showRemoveAllChannelAlert(){
        val builder = AlertDialog.Builder(context)
            .setTitle(getString(R.string.DeleteAllExclusions))
            .setMessage(getString(R.string.DeleteAllExclusionsAlert))
            .setNegativeButton(getString(R.string.Cancel), null)
            .setPositiveButton(getString(R.string.Delete)){_,_ ->
                DahlSettings.wallSettings = DahlSettings.wallSettings.copy(excludedChannels = emptyList())
                listView.adapter.update(true)
            }

        val dialog = builder.create()
        showDialog(dialog)
        (dialog.getButton(DialogInterface.BUTTON_POSITIVE) as? TextView)?.setTextColor(Theme.getColor(Theme.key_text_RedBold))

    }
}