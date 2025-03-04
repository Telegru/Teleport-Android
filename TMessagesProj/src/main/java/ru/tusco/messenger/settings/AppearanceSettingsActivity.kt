package ru.tusco.messenger.settings

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.util.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.telegram.messenger.AndroidUtilities.dp
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment
import org.telegram.ui.LaunchActivity
import org.telegram.ui.WallpapersListActivity
import ru.tusco.messenger.icons.VKUiIconReplacement
import ru.tusco.messenger.settings.DahlSettings.ICON_REPLACEMENT_VKUI
import ru.tusco.messenger.settings.DahlSettings.NO_REPLACEMENT

class AppearanceSettingsActivity : UniversalFragment() {

    companion object {
        const val BOTTOM_PANEL = 1
        const val PERSONAL_COLORS = 2
        const val WALLPAPERS = 3
        const val HIDE_HELP = 4
        const val NAVIGATION_DRAWER = 5
        const val SWITCH_ICONS = 6
    }

    private val icons: Set<Int> by lazy {
        val icons = mutableSetOf<Int>()
        val seenValues = mutableSetOf<Int>()
        VKUiIconReplacement().replacements.forEach { key, value ->
            if (key != R.drawable.attach_send && !seenValues.contains(value)) {
                icons.add(key)
                seenValues.add(value)
            }
        }
        return@lazy icons
    }


    override fun getTitle(): CharSequence = getString(R.string.Appearance)

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {

//        items?.add(UItem.asHeader(getString(R.string.EnableInChatsAndChannels)))
//        items?.add(
//            UItem.asCheck(BOTTOM_PANEL, getString(R.string.BottomPanelInChannels)).setChecked(DahlSettings.isShowBottomPanelInChannels)
//        )
//        items?.add(UItem.asCheck(PERSONAL_COLORS, getString(R.string.PersonalColors)).setChecked(DahlSettings.isEnabledPersonalColors))
//
//        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.Profile)))
        items?.add(UItem.asButton(WALLPAPERS, getString(R.string.Wallpapers)))

        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.TgSettingsMenu)))
        items?.add(UItem.asCheck(HIDE_HELP, getString(R.string.HideHelpBlock)).setChecked(DahlSettings.isHiddenHelpBlock))

        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.NavigationDrawer)))
        items?.add(UItem.asButton(NAVIGATION_DRAWER, getString(R.string.NavigationDrawerItems)))

        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asHeader(getString(R.string.Icons)))
        items?.add(
            UItem.asCheck(SWITCH_ICONS, getString(R.string.IconsVkUI)).apply {
                hideDivider = true
                setChecked(DahlSettings.iconReplacement == ICON_REPLACEMENT_VKUI)
            })
        items?.add(UItem.asCustom(createIconsView()))
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
//        var showRestartAppMessage = false
        when (item?.id) {
            BOTTOM_PANEL -> DahlSettings.isShowBottomPanelInChannels = !DahlSettings.isShowBottomPanelInChannels
            PERSONAL_COLORS -> DahlSettings.isEnabledPersonalColors = !DahlSettings.isEnabledPersonalColors
            WALLPAPERS -> presentFragment(WallpapersListActivity(WallpapersListActivity.TYPE_ALL))
            HIDE_HELP -> DahlSettings.isHiddenHelpBlock = !DahlSettings.isHiddenHelpBlock
            NAVIGATION_DRAWER -> presentFragment(NavigationDrawerSettingsActivity())
            SWITCH_ICONS -> {
                if (item.checked) {
                    DahlSettings.iconReplacement = NO_REPLACEMENT
                } else {
                    DahlSettings.iconReplacement = ICON_REPLACEMENT_VKUI
                }
                (context as LaunchActivity).reloadResources()
//                showRestartAppMessage = true
            }

            else -> {}
        }
//        if (showRestartAppMessage) {
//            Toast.makeText(context, getString(R.string.RestartToast), Toast.LENGTH_SHORT).show()
//        }
        listView.adapter.update(true)
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean = false

    private fun createIconsView(): View {
        val listView = RecyclerView(context)
        listView.id = View.generateViewId()
        listView.background = Theme.createRoundRectDrawable(dp(6f), dp(6f), Theme.getColor(Theme.key_dialogBackgroundGray))
        listView.setHasFixedSize(true)
        listView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val iconsAdapter = IconsAdapter()
        listView.adapter = iconsAdapter
        iconsAdapter.updateIcons(icons.toList())

        val container = FrameLayout(context)
        container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))
        container.id = View.generateViewId()
        val margin = 21f
        container.addView(listView, LayoutHelper.createFrame(LayoutParams.MATCH_PARENT, 40f, Gravity.CENTER, margin, 8f, margin, 16f))
        return container
    }

}

private class IconsAdapter : RecyclerView.Adapter<IconViewHolder>() {

    private var icons = emptyList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val frameLayout = FrameLayout(parent.context)
        frameLayout.id = View.generateViewId()
        return IconViewHolder(frameLayout)
    }

    override fun getItemCount(): Int = icons.size

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateIcons(icons: List<Int>) {
        this.icons = icons
        notifyDataSetChanged()
    }
}

private class IconViewHolder(container: FrameLayout) : RecyclerView.ViewHolder(container) {

    private val iconView: ImageView = ImageView(container.context).apply { id = View.generateViewId() }

    init {
        iconView.imageTintList = ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon))
        container.addView(iconView, LayoutHelper.createFrame(16, 16f, Gravity.CENTER, 12f, 12f, 12f, 12f))
    }

    fun bind(@DrawableRes icon: Int) {
        iconView.setImageResource(icon)
    }
}