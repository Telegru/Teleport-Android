package ru.tusco.messenger.settings

import android.view.View
import org.telegram.messenger.R
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment
import org.telegram.messenger.LocaleController.getString
import java.util.ArrayList

class PremiumSettingsActivity: UniversalFragment() {

    companion object{
        const val SWITCH_HIDE = 1
        const val SWITCH_STATUSES_ICONS = 2
        const val SWITCH_ANIMATED_AVATARS = 3
        const val SWITCH_CUSTOM_WALLPAPERS_IN_CHANNELS = 4
        const val SWITCH_ANIMATED_REACTIONS = 5
        const val SWITCH_ANIMATED_PREMIUM_STICKERS = 6
        const val SWITCH_TOUCH_ON_PREMIUM_STICKER = 7
        const val SWITCH_HIDE_STORIES = 8
        const val SWITCH_HIDE_ADD_STORY = 9
    }

    override fun getTitle(): CharSequence = getString(R.string.PremiumFeatures)

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {
//        items?.add(UItem.asHeader(getString(R.string.BlockPremium)))
//        items?.add(UItem.asCheck(SWITCH_HIDE, getString(R.string.HideFromSettings)).setChecked(DahlSettings.hidePremium))
//        items?.add(UItem.asShadow(getString(R.string.HidePremiumInfo)))
//
//        items?.add(UItem.asHeader(getString(R.string.General)))
//        items?.add(UItem.asCheck(SWITCH_STATUSES_ICONS, getString(R.string.StatusIcons)).setChecked(DahlSettings.statusesIcons))
//        items?.add(UItem.asCheck(SWITCH_ANIMATED_AVATARS, getString(R.string.AnimatedAvatars)).setChecked(DahlSettings.animatedAvatars))
//        items?.add(UItem.asCheck(SWITCH_CUSTOM_WALLPAPERS_IN_CHANNELS, getString(R.string.CustomChannelsWallpapers)).setChecked(DahlSettings.customChannelsWallpapers))
//        items?.add(UItem.asCheck(SWITCH_ANIMATED_REACTIONS, getString(R.string.AnimatedReactions)).setChecked(DahlSettings.animatedReactions))
//        items?.add(UItem.asCheck(SWITCH_ANIMATED_PREMIUM_STICKERS, getString(R.string.AnimatedPremiumStickers)).setChecked(DahlSettings.animatedPremiumStickers))
//        items?.add(UItem.asCheck(SWITCH_TOUCH_ON_PREMIUM_STICKER, getString(R.string.TouchPremiumStickers)).setChecked(DahlSettings.touchOnPremiumStickers))
//
//        items?.add(UItem.asShadow(-3, null))
//
//        items?.add(UItem.asHeader(getString(R.string.Stories)))
//        items?.add(UItem.asCheck(SWITCH_HIDE_STORIES, getString(R.string.HideStories)).setChecked(DahlSettings.hideStories))
//        items?.add(UItem.asCheck(SWITCH_HIDE_ADD_STORY, getString(R.string.HideAddStory)).setChecked(DahlSettings.hideAddStory))
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
        when(item?.id){
            SWITCH_HIDE -> DahlSettings.hidePremium = !DahlSettings.hidePremium
            SWITCH_STATUSES_ICONS -> DahlSettings.statusesIcons = !DahlSettings.statusesIcons
            SWITCH_ANIMATED_AVATARS -> DahlSettings.animatedAvatars = !DahlSettings.animatedAvatars
            SWITCH_CUSTOM_WALLPAPERS_IN_CHANNELS -> DahlSettings.customChannelsWallpapers = !DahlSettings.customChannelsWallpapers
            SWITCH_ANIMATED_REACTIONS -> DahlSettings.animatedReactions = !DahlSettings.animatedReactions
            SWITCH_ANIMATED_PREMIUM_STICKERS -> DahlSettings.animatedPremiumStickers = !DahlSettings.animatedPremiumStickers
            SWITCH_TOUCH_ON_PREMIUM_STICKER -> DahlSettings.touchOnPremiumStickers = !DahlSettings.touchOnPremiumStickers
            SWITCH_HIDE_STORIES -> DahlSettings.hideStories = !DahlSettings.hideStories
            SWITCH_HIDE_ADD_STORY -> DahlSettings.hideAddStory = !DahlSettings.hideAddStory
        }
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean = false
}