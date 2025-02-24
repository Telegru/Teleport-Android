package ru.tusco.messenger.settings

import android.view.View
import org.telegram.messenger.LocaleController.getString
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Cells.TextCheckCell
import org.telegram.ui.Components.UItem
import org.telegram.ui.Components.UniversalAdapter
import org.telegram.ui.Components.UniversalFragment

class GeneralSettingsActivity : UniversalFragment() {

    companion object {
        const val PROXY = 1
        const val HIDE_PHONE_NUMBER = 2
        const val MESSAGE_READ_STATUS = 3
        const val OFFLINE_MODE = 4
        const val PREMIUM = 5
    }

    override fun getTitle(): CharSequence = getString(R.string.General)

    override fun fillItems(items: ArrayList<UItem>?, adapter: UniversalAdapter?) {

//        items?.add(UItem.asHeader(getString(R.string.ConnectionType)))
//        items?.add(UItem.asCheck(PROXY, getString(R.string.ProxyDahl)).setChecked(DahlSettings.isProxyEnabled))
//        items?.add(UItem.asShadow(getString(R.string.ProxyDahlInfo)))
//
//        items?.add(UItem.asHeader(getString(R.string.Profile)))
//        items?.add(UItem.asCheck(HIDE_PHONE_NUMBER, getString(R.string.HidePhoneNumber)).setChecked(DahlSettings.isHidePhoneNumber))
//        items?.add(UItem.asShadow(getString(R.string.HidePhoneNumberInfo)))
//
//        items?.add(UItem.asHeader(getString(R.string.Privacy)))
//        items?.add(
//            UItem.asCustom(
//                MESSAGE_READ_STATUS,
//                TextCheckCell(context).apply {
//                    setTextAndValueAndCheck(
//                        getString(R.string.MessagesStatus),
//                        getString(R.string.MessagesStatusInfo),
//                        DahlSettings.hideMessageReadStatus,
//                        true,
//                        true
//                    )
//                    setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite))
//                }
//            )
//        )
//        items?.add(
//            UItem.asCustom(
//                OFFLINE_MODE,
//                TextCheckCell(context).apply {
//                    setTextAndValueAndCheck(
//                        getString(R.string.OfflineMode),
//                        getString(R.string.OfflineModeInfo),
//                        DahlSettings.isOffline,
//                        true,
//                        false
//                    )
//                    setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite))
//                }
//            )
//        )
//
//        items?.add(UItem.asShadow(-3, null))

        items?.add(UItem.asButton(PREMIUM, getString(R.string.PremiumFeatures)))
        items?.add(UItem.asShadow(getString(R.string.PremiumFeaturesInfo)))
    }

    override fun onClick(item: UItem?, view: View?, position: Int, x: Float, y: Float) {
        when(item?.id){
            PROXY -> DahlSettings.isProxyEnabled = !DahlSettings.isProxyEnabled
            HIDE_PHONE_NUMBER -> DahlSettings.isHidePhoneNumber = !DahlSettings.isHidePhoneNumber
            MESSAGE_READ_STATUS -> DahlSettings.hideMessageReadStatus = !DahlSettings.hideMessageReadStatus
            OFFLINE_MODE -> DahlSettings.isOffline = !DahlSettings.isOffline
            PREMIUM -> presentFragment(PremiumSettingsActivity())
            else -> {}
        }
        listView.adapter.update(true)
    }

    override fun onLongClick(item: UItem?, view: View?, position: Int, x: Float, y: Float): Boolean = false


}