package ru.tusco.messenger.ui.wall

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.exoplayer2.util.Log
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
import org.telegram.ui.ActionBar.BackDrawable
import ru.tusco.messenger.ui.mvvm.BaseStateFragment
import ru.tusco.messenger.ui.mvvm.ViewModelFactory

class WallFragment: BaseStateFragment<WallState, WallViewModel>() {

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
        val contentView = FrameLayout(context)
        return contentView
    }


    override fun createViewModel(): WallViewModel =
        ViewModelFactory.getViewModel(WallViewModel::class.java)

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun renderState(state: WallState) {

    }


}