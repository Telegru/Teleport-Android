package ru.tusco.messenger.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ImageReceiver
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.RecyclerListView
import org.telegram.ui.Stories.StoriesUtilities.AvatarStoryParams
import ru.tusco.messenger.settings.DahlSettings

class RecentChatsPanel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var emptyView: AppCompatTextView

    init {
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite))

        emptyView = AppCompatTextView(context)
        emptyView.text = LocaleController.getString(R.string.EmptyRecentChats)
        emptyView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val margin = AndroidUtilities.dp(16f)
        emptyView.setPadding(margin, 0, margin, 0)
        addView(emptyView, LayoutHelper.createFrame(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        emptyView.visibility = View.GONE
    }

}

internal class RecentChatViewHolder(view: View, private val delegate: Delegate) : RecyclerListView.Holder(view) {

    interface Delegate {

        fun openChat(dialogId: Long)
        fun showContextMenu(dialogId: Long)
    }

    private var dialogId: Long? = null
    private val avatarImage: ImageReceiver = ImageReceiver(itemView)
    private val avatarDrawable = AvatarDrawable()

    private val avatarParams: AvatarStoryParams = object : AvatarStoryParams(false) {
        override fun openStory(dialogId: Long, onDone: Runnable) {
            delegate.openChat(dialogId)
        }

        override fun onLongPress() {
            dialogId?.let { delegate.showContextMenu(it) }
        }
    }

    init {
        avatarImage.setRoundRadius(AndroidUtilities.dp(if (DahlSettings.rectangularAvatars) 3f else 16f))
        avatarImage.setAllowLoadingOnAttachedOnly(true)
    }

    fun bind() {

    }


}