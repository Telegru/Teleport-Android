package ru.tusco.messenger.ui.mvvm

import org.telegram.ui.ActionBar.BaseFragment

abstract class BaseStateFragment<T: Any, VM: BaseViewModel<T>>: BaseFragment() {

    protected lateinit var viewModel: VM

    abstract fun createViewModel(): VM

    abstract fun renderState(state: T)

    override fun onFragmentCreate(): Boolean {
        viewModel = createViewModel()
        return super.onFragmentCreate()
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        ViewModelFactory.destroyViewModel(viewModel::class.java)
    }

    private val stateObserver = { state: T -> renderState(state) }

    override fun setParentFragment(fragment: BaseFragment?) {
        super.setParentFragment(fragment)
        viewModel.state.observe(stateObserver)
    }

    override fun onRemoveFromParent() {
        super.onRemoveFromParent()
        viewModel.state.removeObserver(stateObserver)
    }

}