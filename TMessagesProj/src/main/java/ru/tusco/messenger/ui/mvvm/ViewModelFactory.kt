package ru.tusco.messenger.ui.mvvm

import ru.tusco.messenger.ui.wall.WallViewModel

object ViewModelFactory {
    private val viewModels = mutableMapOf<Class<*>, BaseViewModel<*>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseViewModel<*>> getViewModel(modelClass: Class<T>, vararg args: Any): T {
        if (!viewModels.containsKey(modelClass)) {
            val viewModel = when (modelClass) {
                WallViewModel::class.java -> WallViewModel()
                else ->throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
            viewModels[modelClass] = viewModel
        }
        return viewModels[modelClass] as T
    }

    fun destroyViewModel(modelClass: Class<*>) {
        viewModels[modelClass]?.onDestroy()
        viewModels.remove(modelClass)
    }
}