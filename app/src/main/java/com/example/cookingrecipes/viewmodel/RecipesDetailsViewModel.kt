package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.RecipesDetailsModel
import com.example.cookingrecipes.viewmodel.data.RecipesListForUI
import com.example.cookingrecipes.model.RecipesModel
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecipesDetailsViewModel @Inject constructor(private val recipesDetailsModel: RecipesDetailsModel) : ViewModel() {

    var recipesDetailsResult: MutableLiveData<CookingRecipes> = MutableLiveData()
    var recipesDetailsError: MutableLiveData<String> = MutableLiveData()
    var recipesDetailsLoader: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var disposableObserver: DisposableObserver<CookingRecipes>

    fun recipesDetailsResult(): LiveData<CookingRecipes> {
        return recipesDetailsResult
    }

    fun recipesDetailsError(): LiveData<String> {
        return recipesDetailsError
    }

    fun recipesDetailsLoader(): LiveData<Boolean> {
        return recipesDetailsLoader
    }

    fun loadRecipesDetails(id: Int) {

        disposableObserver = object : DisposableObserver<CookingRecipes>() {
            override fun onComplete() {

            }

            override fun onNext(recipesDetails: CookingRecipes) {
                recipesDetailsResult.postValue(recipesDetails)
                recipesDetailsLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                recipesDetailsError.postValue(e.message)
                recipesDetailsLoader.postValue(false)
            }
        }

        recipesDetailsModel.getRecipesDetails(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableObserver)
    }
}


