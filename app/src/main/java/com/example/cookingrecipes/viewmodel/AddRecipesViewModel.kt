package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.AddRecipesModel
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddRecipesViewModel @Inject constructor(private val addRecipesModel: AddRecipesModel) : ViewModel() {

    var recipesAddResult: MutableLiveData<Long> = MutableLiveData()
    var recipesAddError: MutableLiveData<String> = MutableLiveData()
    lateinit var disposableObserver: SingleObserver<Long>

    fun recipesAddResult(): LiveData<Long> {
        return recipesAddResult
    }

    fun recipesAddError(): LiveData<String> {
        return recipesAddError
    }

    fun addRecipesDetails(cookingRecipes: CookingRecipes) {

        disposableObserver = object : SingleObserver<Long> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(t: Long) {
                recipesAddResult.postValue(t)
            }

            override fun onError(e: Throwable) {
                recipesAddError.postValue(e.message)
            }
        }

        addRecipesModel.addRecipesData(cookingRecipes)!!.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe(disposableObserver)
    }
}
