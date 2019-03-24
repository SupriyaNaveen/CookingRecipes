package com.example.cookingrecipes.views

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseActivity : AppCompatActivity() {

    private val mSubscriptions = CompositeDisposable()

    fun subscribe(disposable: Disposable): Disposable {
        mSubscriptions.add(disposable)
        return disposable
    }

    override fun onStop() {
        super.onStop()
        mSubscriptions.clear()
    }
}