package ru.tusco.messenger.ui.wall

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.telegram.ui.Cells.BaseCell
import org.telegram.ui.Components.RecyclerListView

class WallListAdapter: RecyclerListView.SelectionAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun isEnabled(holder: RecyclerView.ViewHolder): Boolean = true
}

class WallMessageCell(context: Context) : BaseCell(context){

    private var offsetY: Float = 0f


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

    }

}