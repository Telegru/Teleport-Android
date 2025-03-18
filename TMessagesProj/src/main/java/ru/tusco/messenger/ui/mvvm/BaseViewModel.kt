package ru.tusco.messenger.ui.mvvm

import androidx.annotation.UiThread

@UiThread
abstract class BaseViewModel<VS>(initialState: VS) {

    val state = ViewState(initialState)

    var isDestroyed = false
        private set

    open fun onDestroy() {
        isDestroyed = true
    }

}