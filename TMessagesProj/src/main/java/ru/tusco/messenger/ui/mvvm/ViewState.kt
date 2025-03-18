package ru.tusco.messenger.ui.mvvm

import androidx.annotation.UiThread

@UiThread
class ViewState<T>(initial: T) {

    var value: T = initial
        private set

    private val observers = mutableListOf<(T) -> Unit>()

    fun setValue(newValue: T) {
        value = newValue
        notifyObservers()
    }

    fun observe(observer: (T) -> Unit) {
        if (observer !in observers) {
            observers.add(observer)
            observer(value)
        }
    }

    fun removeObserver(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    private fun notifyObservers() {
        observers.forEach { it(value) }
    }
}