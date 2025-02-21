package ru.tusco.messenger.settings

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment

class DahlSettingsActivity : UniversalFragment() {

    companion object {
        const val GENERAL = 1
        const val APPEARANCE = 2
        const val CHATS = 3
        const val SUPPORT = 4
    }

    private var iconContainer: FrameLayout? = null
    private var icon: ImageView? = null

    override fun getTitle(): CharSequence {
        return ""
    }

    override fun createActionBar(context: Context): ActionBar {
        val actionBar = super.createActionBar(context)

        return actionBar
    }

    override fun createView(context: Context?): View {
        return super.createView(context)
    }

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
        items?.add(UItem.asHeader(getString(R.string.Categories)))
        items?.add(UItem.asButton(GENERAL, R.drawable.all_categories_outline_28, getString(R.string.General)))
        items?.add(UItem.asButton(APPEARANCE, R.drawable.palette_outline_28, getString(R.string.Appearance)))
        items?.add(UItem.asButton(CHATS, R.drawable.msg2_discussion, getString(R.string.ChatsSettings)))

        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.Links)))
        items?.add(UItem.asButton(SUPPORT, R.drawable.message_heart_outline_28, getString(R.string.Support), "@dahl_help"))
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
        when (item?.id) {
            GENERAL -> presentFragment(GeneralSettingsActivity())
            CHATS -> presentFragment(ChatsSettingsActivity())
            SUPPORT -> showSupportAlert()
            else -> {}
        }
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean {
        return false
    }

    private fun showSupportAlert() {
        val dialog = AlertDialog.Builder(parentActivity)
            .setTitle(getString(R.string.ContactSupport))
            .setMessage(getString(R.string.DahlSupportInfo))
            .setNegativeButton(getString(R.string.Cancel), null)
            .setPositiveButton(getString(R.string.SendMessage)) { _, _ ->
                MessagesController.getInstance(currentAccount).openByUserName(("dahl_help"), this, 1)
            }
            .create()

        showDialog(dialog)
    }

}
