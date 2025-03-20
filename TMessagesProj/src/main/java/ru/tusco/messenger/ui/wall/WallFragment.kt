package ru.tusco.messenger.ui.wall

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
import org.telegram.ui.ActionBar.BackDrawable
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.RecyclerListView
import org.telegram.ui.Components.SizeNotifierFrameLayout
import ru.tusco.messenger.ui.mvvm.BaseStateFragment
import ru.tusco.messenger.ui.mvvm.ViewModelFactory

class WallFragment: BaseStateFragment<WallState, WallViewModel>() {

    private var contentView: SizeNotifierFrameLayout? = null
    private var listView: RecyclerListView? = null
    private var adapter: WallListAdapter? = null

    override fun createView(context: Context): View {
        actionBar.backButtonDrawable = BackDrawable(false)
        actionBar.setAllowOverlayTitle(true)
        actionBar.setTitle("Стена")
        actionBar.setActionBarMenuOnItemClick(object : ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    finishFragment()
                }
            }
        })
        val contentView = SizeNotifierFrameLayout(context)

        listView = RecyclerListView(parentActivity)
        adapter = WallListAdapter(currentAccount, loadNextPage = {})
        listView?.adapter = adapter
        listView?.layoutManager = LinearLayoutManager(parentActivity)

        contentView.addView(listView, LayoutHelper.createFrame(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT.toFloat()))
        this.contentView = contentView
        updateBackground()


        return contentView
    }


    override fun createViewModel(): WallViewModel =
        ViewModelFactory.getViewModel(WallViewModel::class.java)


    override fun renderState(state: WallState) {
        Log.d("WallFragment", "renderState")
        adapter?.insert(state.data)

    }

    private fun updateBackground() {
//        if (contentView?.backgroundImage != null) {
//            return
//        }
        contentView?.background = Theme.getCachedWallpaperNonBlocking()
//        contentView?.setBackgroundImage(Theme.getCachedWallpaperNonBlocking(), Theme.isWallpaperMotion())
    }

}