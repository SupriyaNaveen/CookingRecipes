package com.example.cookingrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cookingrecipes.model.RecipesModel
import com.example.cookingrecipes.model.data.CookingRecipes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecipesViewModel @Inject constructor(private val recipesModel: RecipesModel) : ViewModel() {

    var recipesResult: MutableLiveData<List<CookingRecipes>> = MutableLiveData()
    var receipesError: MutableLiveData<String> = MutableLiveData()
    var recipesLoader: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var disposableObserver: DisposableObserver<List<CookingRecipes>>

    fun recipesResult(): LiveData<List<CookingRecipes>> {
        return recipesResult
    }

    fun recipesError(): LiveData<String> {
        return receipesError
    }

    fun recipesLoader(): LiveData<Boolean> {
        return recipesLoader
    }

    fun loadRecipes(limit: Int, offset: Int) {

        disposableObserver = object : DisposableObserver<List<CookingRecipes>>() {
            override fun onComplete() {

            }

            override fun onNext(recipesList: List<CookingRecipes>) {
                recipesResult.postValue(recipesList)
                recipesLoader.postValue(false)
            }

            override fun onError(e: Throwable) {
                receipesError.postValue(e.message)
                recipesLoader.postValue(false)
            }
        }

        recipesModel.getRecipesOnLimit(limit, offset)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(disposableObserver)
    }


    fun disposeElements() {
        try {
            if (this::disposableObserver.isInitialized) {
                if (!disposableObserver.isDisposed) disposableObserver.dispose()
            }
        } catch (e: UninitializedPropertyAccessException) {
            Timber.e(e)
        }
    }
}


