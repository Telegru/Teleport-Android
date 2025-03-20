package ru.tusco.messenger.ui.mvvm

import org.telegram.ui.ActionBar.BaseFragment

abstract class BaseStateFragment<T: Any, VM: BaseViewModel<T>>: BaseFragment() {

    protected lateinit var viewModel: VM

    abstract fun createViewModel(): VM

    abstract fun renderState(state: T)

    private val stateObserver = { state: T -> renderState(state) }

    override fun onFragmentCreate(): Boolean {
        viewModel = createViewModel()
        viewModel.state.observe(stateObserver)
        return super.onFragmentCreate()
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        viewModel.state.removeObserver(stateObserver)
        ViewModelFactory.destroyViewModel(viewModel::class.java)
    }

}