package io.trewartha.positional.ui

import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {

    private val disposables = CompositeDisposable()

    @CallSuper
    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    protected fun Disposable.attach() {
        disposables.add(this)
    }

}